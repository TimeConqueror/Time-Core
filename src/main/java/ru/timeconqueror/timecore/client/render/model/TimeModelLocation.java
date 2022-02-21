package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.util.ResourceLocation;

public class TimeModelLocation {
    private final ResourceLocation location;
    private final String modelName;
    public static final String WILDCARD = "*";

    public TimeModelLocation(ResourceLocation location, String modelName) {
        this.location = location;
        this.modelName = modelName;
    }

    public static TimeModelLocation wildcarded(ResourceLocation location) {
        return new TimeModelLocation(location, WILDCARD);
    }

    public boolean isWildcard() {
        return modelName.equals(WILDCARD);
    }

    public ResourceLocation location() {
        return location;
    }

    public String modelName() {
        return modelName;
    }

    @Override
    public String toString() {
        if (isWildcard()) {
            return location.toString() + "|(any)";
        }

        return location.toString() + "|" + modelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeModelLocation)) return false;

        TimeModelLocation that = (TimeModelLocation) o;

        if (!location.equals(that.location)) return false;
        return modelName.equals(that.modelName);
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + modelName.hashCode();
        return result;
    }
}
