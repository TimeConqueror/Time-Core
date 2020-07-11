package ru.timeconqueror.timecore.registry.deferred.base;

import net.minecraftforge.eventbus.api.IEventBus;

public abstract class DeferredTimeRegister {
    private final String modid;

    public DeferredTimeRegister(String modid) {
        this.modid = modid;
    }

    public abstract void regToBus(IEventBus bus);

    public String getModid() {
        return modid;
    }
}
