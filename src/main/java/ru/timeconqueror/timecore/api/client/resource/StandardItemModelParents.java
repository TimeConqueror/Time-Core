package ru.timeconqueror.timecore.api.client.resource;

import net.minecraft.util.ResourceLocation;

public enum StandardItemModelParents {
    DEFAULT(new ResourceLocation("item/generated")),
    HANDHELD(new ResourceLocation("item/handheld"));

    private ResourceLocation resourceLocation;

    StandardItemModelParents(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}