package ru.timeconqueror.timecore.api.client;

import net.minecraftforge.fml.ModList;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;

public class TimeClient {
    public static final TimeResourceHolder RESOURCE_HOLDER = new TimeResourceHolder();

    static {
        setup();
    }

    public static void setup() {
        ModList.get().forEachModContainer((s, modContainer) -> {
            if (modContainer.getMod() instanceof ITimeMod) {
                RESOURCE_HOLDER.addDomain(s);
            }
        });
    }
}
