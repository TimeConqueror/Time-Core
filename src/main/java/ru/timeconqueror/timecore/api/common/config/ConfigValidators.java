package ru.timeconqueror.timecore.api.common.config;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public class ConfigValidators {
    private static final Predicate<Object> RESOURCE_LOCATION = o -> {
        if (!(o instanceof String)) {
            return false;
        }
        String str = (String) o;

        return ResourceLocation.tryParse(str) != null;
    };

    public static Predicate<Object> resourceLocation() {
        return RESOURCE_LOCATION;
    }
}
