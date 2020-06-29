package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.util.ResourceLocation;

public class LocationResolver {
    public static ResourceLocation toItemModelLocation(ResourceLocation registryName) {
        return new ResourceLocation(registryName.getNamespace(), "models/item/" + registryName.getPath() + ".json");
    }

    public static ResourceLocation toBlockStateLocation(ResourceLocation registryName) {
        return new ResourceLocation(registryName.getNamespace(), "blockstates/" + registryName.getPath() + ".json");
    }
}
