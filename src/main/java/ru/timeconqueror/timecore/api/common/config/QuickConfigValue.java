package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class QuickConfigValue<T> implements ILoadListener {
    private final ForgeConfigSpec.ConfigValue<T> configValue;
    private T currentVal;
    private boolean invalidated = true;

    QuickConfigValue(ForgeConfigSpec.ConfigValue<T> configValue) {
        this.configValue = configValue;
    }

    public T get() {
        if (invalidated) {
            currentVal = configValue.get();
        }
        return currentVal;
    }

    public void set(T value) {
        configValue.set(value);
        invalidate();
    }

    public void invalidate() {
        invalidated = true;
    }

    @Override
    public void onEveryLoad(ModConfig.ModConfigEvent event) {
        invalidate();
    }
}
