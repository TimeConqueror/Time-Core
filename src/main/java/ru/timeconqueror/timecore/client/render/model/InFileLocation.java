package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.resources.ResourceLocation;

public record InFileLocation(ResourceLocation location, String name) {
    public static final String WILDCARD = "*";

    public static InFileLocation wildcarded(ResourceLocation location) {
        return new InFileLocation(location, WILDCARD);
    }

    public boolean isWildcard() {
        return name.equals(WILDCARD);
    }

    @Override
    public String toString() {
        if (isWildcard()) {
            return location.toString() + "#(any)";
        }

        return location.toString() + "#" + name;
    }
}
