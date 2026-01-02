package ru.timeconqueror.timecore.api.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Utils {
    public static boolean isValidResourceLocation(String location) {
        return ResourceLocation.tryParse(location) != null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registryKey) {
       return (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey)registryKey);//FIXME check
    }

    public static ResourceLocation getKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation getKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
