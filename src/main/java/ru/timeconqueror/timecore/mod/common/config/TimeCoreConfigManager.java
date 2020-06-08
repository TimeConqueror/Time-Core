package ru.timeconqueror.timecore.mod.common.config;

import ru.timeconqueror.timecore.api.common.config.ConfigManager;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class TimeCoreConfigManager extends ConfigManager {
    protected void register() {
        registerConfig(MainConfig.INSTANCE);
    }
}
