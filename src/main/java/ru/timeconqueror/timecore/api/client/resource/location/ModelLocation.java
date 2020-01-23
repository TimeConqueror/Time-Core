package ru.timeconqueror.timecore.api.client.resource.location;

import net.minecraft.util.ResourceLocation;

/**
 * Represents the location of the model.
 *
 * @see BlockModelLocation - model location for block.
 * @see ItemModelLocation - model location for item.
 */
public abstract class ModelLocation extends AdaptiveLocation {
    public ModelLocation(String modid, String path) {
        super(modid, path);
    }

    public ResourceLocation fullLocation() {
        return new ResourceLocation(getNamespace(), getFullPath() + ".json");
    }
}
