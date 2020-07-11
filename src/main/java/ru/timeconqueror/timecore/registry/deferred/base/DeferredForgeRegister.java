package ru.timeconqueror.timecore.registry.deferred.base;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DeferredForgeRegister<T extends IForgeRegistryEntry<T>> extends DeferredTimeRegister {
    private final IForgeRegistry<T> registry;

    public DeferredForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(modid);
        registry = reg;
    }

    protected abstract EventPriority getRegPriority();

    protected abstract void onRegEvent(RegistryEvent.Register<T> event);

    @Override
    public void regToBus(IEventBus bus) {
        bus.addListener(getRegPriority(), this::onAllRegEvent);
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
