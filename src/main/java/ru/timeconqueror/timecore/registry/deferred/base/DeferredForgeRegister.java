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

    public DeferredForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(modid);
        registry = reg;
    }

    protected abstract EventPriority getRegPriority();

    protected void addClientSetupTask(Runnable runnable) {
        if (EnvironmentUtils.isOnClient()) {
            clientRunnables.add(runnable);
        }
    }

    protected void onRegEvent(RegistryEvent.Register<T> event) {
    }

    protected void onClientInit(FMLClientSetupEvent event) {
        clientRunnables.forEach(Runnable::run);
        clientRunnables = null;
    }

    @Override
    public void regToBus(IEventBus bus) {
        bus.addListener(getRegPriority(), this::onAllRegEvent);
        bus.addListener(getRegPriority(), this::onClientInit);
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
