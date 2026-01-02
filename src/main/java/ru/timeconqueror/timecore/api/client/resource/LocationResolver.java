package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.resources.ResourceLocation;

public class LocationResolver {
    public static ResourceLocation toItemModelLocation(ResourceLocation registryName) {
        return ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "models/item/" + registryName.getPath() + ".json");
    }

    public static ResourceLocation toBlockStateLocation(ResourceLocation registryName) {
        return ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "blockstates/" + registryName.getPath() + ".json");
    }
}
