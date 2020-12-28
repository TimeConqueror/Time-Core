package ru.timeconqueror.timecore.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import ru.timeconqueror.timecore.api.common.config.IConfigValDelegate;
import ru.timeconqueror.timecore.api.common.config.IQuickConfigValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class QuickConfigValue<T> implements IQuickConfigValue<T> {
    private final IConfigValDelegate<T> valueDelegate;
    private T currentVal;
    private boolean invalidated = true;

    protected QuickConfigValue(IConfigValDelegate<T> valueDelegate) {
        this.valueDelegate = valueDelegate;
    }

    public static <T> IQuickConfigValue<T> fromConfigValue(ForgeConfigSpec.ConfigValue<T> configValue) {
        return new QuickConfigValue<>(new ConfigValDelegate<>(configValue));
    }

    public static <T> IQuickConfigValue<T> fromConverter(Supplier<T> deserializer, Consumer<T> serializer) {
        return new QuickConfigValue<>(new MappedDelegate<>(deserializer, serializer));
    }

    public T get() {
        if (invalidated) {
            currentVal = valueDelegate.get();
        }
        return currentVal;
    }

    public void set(T value) {
        valueDelegate.set(value);
        invalidate();
    }

    public void invalidate() {
        invalidated = true;
    }

    @Override
    public void onEveryLoad(ModConfig.ModConfigEvent event) {
        invalidate();
    }

    public static class ConfigValDelegate<T> implements IConfigValDelegate<T> {
        private final ForgeConfigSpec.ConfigValue<T> configValue;

        public ConfigValDelegate(ForgeConfigSpec.ConfigValue<T> configValue) {
            this.configValue = configValue;
        }

        @Override
        public T get() {
            return configValue.get();
        }

        @Override
        public void set(T val) {
            configValue.set(val);
        }
    }

    public static class MappedDelegate<T> implements IConfigValDelegate<T> {
        private final Supplier<T> from;
        private final Consumer<T> to;

        public MappedDelegate(Supplier<T> from, Consumer<T> to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public T get() {
            return from.get();
        }

        @Override
        public void set(T val) {
            to.accept(val);
        }
    }
}
