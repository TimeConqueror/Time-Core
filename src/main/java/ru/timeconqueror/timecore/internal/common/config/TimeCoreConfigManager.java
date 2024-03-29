package ru.timeconqueror.timecore.internal.common.config;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.ConfigRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class TimeCoreConfigManager {
    @AutoRegistrable
    private static final ConfigRegister REGISTER = new ConfigRegister(TimeCore.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register(MainConfig.INSTANCE);
    }
}
