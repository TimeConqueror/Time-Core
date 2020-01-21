package ru.timeconqueror.timecore.api.util;

import net.minecraft.util.ResourceLocation;

public class ResourceHelper {
    public static ResourceLocation toItemModelLocation(ResourceLocation registryName) {
        return new ResourceLocation(registryName.getNamespace(), "models/item/" + registryName.getPath() + ".json");
    }

    public static ResourceLocation toBlockStateLocation(ResourceLocation registryName) {
        return new ResourceLocation(registryName.getNamespace(), "blockstates/" + registryName.getPath() + ".json");
    }
}
