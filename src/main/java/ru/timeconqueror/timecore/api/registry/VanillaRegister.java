package ru.timeconqueror.timecore.api.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import ru.timeconqueror.timecore.api.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.api.registry.util.Promised;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.api.util.holder.Holder;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.internal.registry.InsertablePromised;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class VanillaRegister<T> extends TimeRegister {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private Map<InsertablePromised<T>, Supplier<T>> entries = new HashMap<>();

    private final TaskHolder<Runnable> clientSetupTasks = TaskHolder.make(FMLClientSetupEvent.class);
    private final TaskHolder<Runnable> regEventTasks = TaskHolder.make(RegisterEvent.class);
    private final TaskHolder<Runnable> commonSetupTasks = TaskHolder.make(FMLCommonSetupEvent.class);

    public VanillaRegister(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        super(modId);
        this.registryKey = registryKey;
    }

    public VanillaRegister(Registry<T> registry, String modId) {
        this(registry.key(), modId);
    }

    public VanillaRegister(IForgeRegistry<T> registry, String modId) {
        this(registry.getRegistryKey(), modId);
    }

    @SuppressWarnings("unchecked")
    protected <I extends T> Promised<I> registerEntry(String name, Supplier<I> entrySup) {
        Preconditions.checkNotNull(entries, "Cannot register new entries after RegistryEvent.Register has been fired.");

        ResourceLocation registryName = new ResourceLocation(getModId(), name);
        InsertablePromised<I> promised = new InsertablePromised<>(registryName);
        if (entries.put((InsertablePromised<T>) promised, entrySup::get) != null) {
            throw new IllegalArgumentException("Attempted to register " + name + " twice for registry " + registryKey.registry());
        }

        return promised;
    }

    protected void runOnClientSetup(Runnable task) {
        if (EnvironmentUtils.isOnPhysicalClient()) {
            clientSetupTasks.add(task);
        }
    }

    protected void runOnCommonSetup(Runnable task) {
        commonSetupTasks.add(task);
    }

    protected void runAfterRegistering(Runnable task) {
        regEventTasks.add(task);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, this::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onAllRegEvent);
    }

    private void onAllRegEvent(RegisterEvent event) {
        if (!event.getRegistryKey().equals(registryKey)) return;
        onRegEvent(event);
    }

    protected void onRegEvent(RegisterEvent event) {
        Holder<Promised<T>> promiseHolder = new Holder<>(null);

        catchErrors("registering " + registryKey.location() + " entries", () -> {
            for (Map.Entry<InsertablePromised<T>, Supplier<T>> entry : entries.entrySet()) {
                InsertablePromised<T> promise = entry.getKey();
                T value = entry.getValue().get();

                promiseHolder.set(promise);
                validateEntry(value);
                event.register(registryKey, promise.getId(), () -> value);

                promise.insert(value);
            }
        }, () -> Lists.newArrayList(
                Pair.of("Registry type", registryKey.location()),
                Pair.of("Currently registering object", promiseHolder.get() != null ? promiseHolder.get().getId() : null)));

        entries = null;

        catchErrors("finishing register event of type " + registryKey.location(), () -> regEventTasks.doForEachAndRemove(Runnable::run));
    }

    /**
     * Throws error if the entry is invalid upon the register event
     */
    protected void validateEntry(T entry) {
    }

    protected void onClientSetup(FMLClientSetupEvent event) {
        enqueueWork(event, () -> clientSetupTasks.doForEachAndRemove(Runnable::run));
    }

    protected void onCommonSetup(FMLCommonSetupEvent event) {
        enqueueWork(event, () -> commonSetupTasks.doForEachAndRemove(Runnable::run));
    }

    protected LangGeneratorFacade getLangGeneratorFacade() {
        return modFeatures.getLangGeneratorFacade();
    }

    public static class RegisterChain<I> {
        protected final Promised<I> promise;

        protected RegisterChain(Promised<I> promise) {
            this.promise = promise;
        }

        public Promised<I> asPromised() {
            return promise;
        }

        public ResourceLocation getRegistryName() {
            return asPromised().getId();
        }

        public String getModId() {
            return getRegistryName().getNamespace();
        }

        public String getName() {
            return getRegistryName().getPath();
        }

        public void clientSideOnly(Runnable runnable) {
            if (EnvironmentUtils.isOnPhysicalClient()) runnable.run();
        }
    }
}
