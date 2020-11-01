package ru.timeconqueror.timecore.registry.newreg;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.api.util.Wrapper;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ForgeRegister<T extends IForgeRegistryEntry<T>> extends TimeRegister {
    private final IForgeRegistry<T> registry;
    private Map<RegistryObject<T>, Supplier<T>> entries = new HashMap<>();
    private List<Runnable> clientRunnables = new ArrayList<>();
    private List<Runnable> regEventRunnables = new ArrayList<>();

    public ForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(modid);
        registry = reg;
    }

    @SuppressWarnings("unchecked")
    protected <I extends T> RegistryObject<I> registerEntry(String name, Supplier<I> entrySup) {
        Preconditions.checkNotNull(entries, "Cannot register new entries after RegistryEvent.Register has been fired.");

        ResourceLocation registryName = new ResourceLocation(getModid(), name);
        RegistryObject<I> holder = RegistryObject.of(registryName, registry);

        if (entries.put((RegistryObject<T>) holder, () -> entrySup.get().setRegistryName(registryName)) != null) {
            throw new IllegalArgumentException("Attempted to register " + name + " twice for registry " + registry.getRegistryName());
        }

        return holder;
    }

    protected void runTaskOnClientSetup(Runnable runnable) {
        if (EnvironmentUtils.isOnPhysicalClient()) {
            Preconditions.checkNotNull(clientRunnables, "You attempted to call this method after FMLClientSetupEvent has been fired.");
            clientRunnables.add(runnable);
        }
    }

    protected void runTaskAfterRegistering(Runnable runnable) {
        Preconditions.checkNotNull(regEventRunnables, "You attempted to call this method after RegistryEvent.Register has been fired.");

        regEventRunnables.add(runnable);
    }

    @Override
    public void regToBus(IEventBus bus) {
        bus.register(new EventDispatcher());
        bus.addListener(EventPriority.LOWEST, this::onClientInit);
    }

    private void onAllRegEvent(RegistryEvent.Register<? extends IForgeRegistryEntry<?>> event) {
        if (event.getGenericType() == registry.getRegistrySuperType()) {
            onRegEvent(((RegistryEvent.Register<T>) event));
        }
    }

    protected void onRegEvent(RegistryEvent.Register<T> event) {
        IForgeRegistry<T> registry = event.getRegistry();

        Wrapper<RegistryObject<T>> currentHolder = new Wrapper<>(null);

        withErrorCatching("registering entries of type " + registry.getRegistrySuperType(), () -> {
            for (Map.Entry<RegistryObject<T>, Supplier<T>> entry : entries.entrySet()) {
                RegistryObject<T> holder = entry.getKey();
                currentHolder.set(holder);
                registry.register(entry.getValue().get());

                holder.updateReference(registry);
            }
        }, () -> Lists.newArrayList(
                Pair.of("Registry type", registry.getRegistrySuperType()),
                Pair.of("Currently registering object", currentHolder.get() != null ? currentHolder.get().getId() : null)));

        entries = null;

        withErrorCatching("finishing register event of type " + registry.getRegistrySuperType(), () -> regEventRunnables.forEach(Runnable::run));

        regEventRunnables = null;
    }

    protected void onClientInit(FMLClientSetupEvent event) {
        withErrorCatching("client setup event", () -> clientRunnables.forEach(Runnable::run));

        clientRunnables = null;
    }

    protected IForgeRegistry<T> getRegistry() {
        return registry;
    }

    public static class RegisterChain<I extends IForgeRegistryEntry<? super I>> {
        protected final RegistryObject<I> holder;
        private final ForgeRegister<?> register;

        protected RegisterChain(ForgeRegister<?> register, RegistryObject<I> holder) {
            this.holder = holder;
            this.register = register;
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

        protected RegisterChain<I> runOnlyForClient(Runnable runnable) {
            register.runTaskOnClientSetup(runnable);
            return this;
        }
    }

    public class EventDispatcher {
        @SubscribeEvent
        public void handleEvent(RegistryEvent.Register<?> event) {
            ForgeRegister.this.onAllRegEvent(event);
        }
    }
}
