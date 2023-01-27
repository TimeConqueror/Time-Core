package ru.timeconqueror.timecore.api.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public class Utils {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registryKey) {
       return (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey)registryKey);//FIXME check
    }
}
