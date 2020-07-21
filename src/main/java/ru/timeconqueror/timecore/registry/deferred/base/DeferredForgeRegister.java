package ru.timeconqueror.timecore.registry.deferred.base;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class DeferredForgeRegister<T extends IForgeRegistryEntry<T>> extends DeferredTimeRegister {
    private final IForgeRegistry<T> registry;
    private List<Runnable> clientRunnables = new ArrayList<>();
    private List<Runnable> regEventRunnables = new ArrayList<>();

    public DeferredForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(modid);
        registry = reg;
    }

    protected void runTaskOnClientSetup(Runnable runnable) {
        if (EnvironmentUtils.isOnPhysicalClient()) {
            clientRunnables.add(runnable);
        }
    }

    protected void runTaskAfterRegistering(Runnable runnable) {
        regEventRunnables.add(runnable);
    }

    protected void onRegEvent(RegistryEvent.Register<T> event) {
        regEventRunnables.forEach(Runnable::run);
        regEventRunnables = null;
    }

    protected void onClientInit(FMLClientSetupEvent event) {
        clientRunnables.forEach(Runnable::run);
        clientRunnables = null;
    }

    @Override
    public void regToBus(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, this::onAllRegEvent);
        bus.addListener(EventPriority.LOWEST, this::onClientInit);
    }

    private void onAllRegEvent(RegistryEvent.Register<?> event) {
        if (event.getGenericType() == registry.getRegistrySuperType()) {
            onRegEvent(((RegistryEvent.Register<T>) event));
        }
    }

    protected IForgeRegistry<T> getRegistry() {
        return registry;
    }
}
