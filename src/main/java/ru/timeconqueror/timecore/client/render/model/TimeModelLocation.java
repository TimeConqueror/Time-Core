package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.resources.ResourceLocation;

public record TimeModelLocation(ResourceLocation location, String modelName) {
    public static final String SINGLE_MODEL_MASK = "i:single";

    public static TimeModelLocation singleModelFile(ResourceLocation location) {
        return new TimeModelLocation(location, SINGLE_MODEL_MASK);
    }

    @Override
    public String toString() {
        if (modelName.equals(SINGLE_MODEL_MASK)) {
            return location.toString() + " (single)";
        }

        return location.toString() + "|" + modelName;
    }
}
