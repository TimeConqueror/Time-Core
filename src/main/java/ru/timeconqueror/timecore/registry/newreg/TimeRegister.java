package ru.timeconqueror.timecore.registry.newreg;

import net.minecraftforge.eventbus.api.IEventBus;

public abstract class TimeRegister {
    private final String modid;

    public TimeRegister(String modid) {
        this.modid = modid;
    }

    public abstract void regToBus(IEventBus bus);

    public String getModid() {
        return modid;
    }
}
