package ru.timeconqueror.timecore.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import ru.timeconqueror.timecore.api.auxiliary.ConfigReloader;

/**
 * This event is fired by {@link ConfigReloader#reloadConfigsFromFile(String, String)}
 * So to reload or process some special things (for example parsing lists), you may handle this event.
 */
public class OnConfigReloadedEvent extends Event {
    private String modid;
    private String fileName;

    public OnConfigReloadedEvent(String modid, String fileName) {
        this.modid = modid;
        this.fileName = fileName;
    }

    public String getModID() {
        return modid;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}