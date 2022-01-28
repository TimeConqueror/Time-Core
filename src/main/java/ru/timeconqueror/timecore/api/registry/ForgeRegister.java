package ru.timeconqueror.timecore.api.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import ru.timeconqueror.timecore.api.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.api.registry.base.TaskHolder;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.api.util.Wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ForgeRegister<T extends IForgeRegistryEntry<T>> extends TimeRegister {
    private final IForgeRegistry<T> registry;
    private Map<RegistryObject<T>, Supplier<T>> entries = new HashMap<>();

    private final TaskHolder<Runnable> clientSetupTasks = TaskHolder.make(FMLClientSetupEvent.class);
    private final TaskHolder<Runnable> regEventTasks = TaskHolder.make(RegistryEvent.Register.class);
    private final TaskHolder<Runnable> commonSetupTasks = TaskHolder.make(FMLCommonSetupEvent.class);

    public ForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(modid);
        registry = reg;
    }

    @SuppressWarnings("unchecked")
    protected <I extends T> RegistryObject<I> registerEntry(String name, Supplier<I> entrySup) {
        Preconditions.checkNotNull(entries, "Cannot register new entries after RegistryEvent.Register has been fired.");

        ResourceLocation registryName = new ResourceLocation(getModId(), name);
        RegistryObject<I> holder = RegistryObject.of(registryName, registry);
        if (entries.put((RegistryObject<T>) holder, () -> entrySup.get().setRegistryName(registryName)) != null) {
            throw new IllegalArgumentException("Attempted to register " + name + " twice for registry " + registry.getRegistryName());
        }

        return holder;
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
        modEventBus.register(new EventDispatcher());
        modEventBus.addListener(EventPriority.LOWEST, this::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onAllRegEvent(RegistryEvent.Register<? extends IForgeRegistryEntry<?>> event) {
        if (event.getGenericType() == registry.getRegistrySuperType()) {
            onRegEvent(((RegistryEvent.Register<T>) event));
        }
    }

    protected void onRegEvent(RegistryEvent.Register<T> event) {
        IForgeRegistry<T> registry = event.getRegistry();

        Wrapper<RegistryObject<T>> currentHolder = new Wrapper<>(null);

        catchErrors("registering entries of type " + registry.getRegistrySuperType(), () -> {
            for (Map.Entry<RegistryObject<T>, Supplier<T>> entry : entries.entrySet()) {
                RegistryObject<T> holder = entry.getKey();
                currentHolder.set(holder);

                T value = entry.getValue().get();
                validateEntry(value);
                registry.register(value);

                holder.updateReference(registry);
            }
        }, () -> Lists.newArrayList(
                Pair.of("Registry type", registry.getRegistrySuperType()),
                Pair.of("Currently registering object", currentHolder.get() != null ? currentHolder.get().getId() : null)));

        entries = null;

        catchErrors("finishing register event of type " + registry.getRegistrySuperType(), () -> regEventTasks.doForEachAndRemove(Runnable::run));
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

    protected IForgeRegistry<T> getRegistry() {
        return registry;
    }


    protected LangGeneratorFacade getLangGeneratorFacade() {
        return modFeatures.getLangGeneratorFacade();
    }

    public static class RegisterChain<I extends IForgeRegistryEntry<? super I>> {
        protected final RegistryObject<I> holder;

        protected RegisterChain(RegistryObject<I> holder) {
            this.holder = holder;
        }

        public RegistryObject<I> asRegistryObject() {
            return holder;
        }

        public ResourceLocation getRegistryName() {
            return asRegistryObject().getId();
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

    public class EventDispatcher {
        @SubscribeEvent
        public void handleEvent(RegistryEvent.Register<?> event) {
            ForgeRegister.this.onAllRegEvent(event);
        }
    }
}
