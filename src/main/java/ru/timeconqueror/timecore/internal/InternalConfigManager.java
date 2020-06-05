package ru.timeconqueror.timecore.internal;

import ru.timeconqueror.timecore.api.common.config.ConfigManager;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class InternalConfigManager extends ConfigManager {
    protected void register() {
        registerConfig(MainConfig.INSTANCE);
    }
}
