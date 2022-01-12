package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.resources.ResourceLocation;

public record TimeModelLocation(ResourceLocation location, String modelName) {
    public static final String WILDCARD = "*";

    public static TimeModelLocation wildcarded(ResourceLocation location) {
        return new TimeModelLocation(location, WILDCARD);
    }

    public boolean isWildcard() {
        return modelName.equals(WILDCARD);
    }

    @Override
    public String toString() {
        if (isWildcard()) {
            return location.toString() + "| (any)";
        }

        return location.toString() + "|" + modelName;
    }
}
