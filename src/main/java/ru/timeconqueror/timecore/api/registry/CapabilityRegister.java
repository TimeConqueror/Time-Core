package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.registry.base.RunnableStoringRegister;

import java.util.concurrent.Callable;

public class CapabilityRegister extends RunnableStoringRegister {
    public CapabilityRegister(String modId) {
        super(modId);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        catchErrors(FMLCommonSetupEvent.class, this::runAll);
    }

    public <T> void regCapability(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory) {
        add(() -> CapabilityManager.INSTANCE.register(type, storage, factory));
    }

    public <T> void regCapability(Class<T> type, Capability.IStorage<T> storage) {
        regCapability(type, storage, () -> null);
    }
}
