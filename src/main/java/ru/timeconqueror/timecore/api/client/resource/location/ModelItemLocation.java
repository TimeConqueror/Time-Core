package ru.timeconqueror.timecore.api.client.resource.location;

import org.jetbrains.annotations.NotNull;

public class ModelItemLocation extends ModelLocation {
    /**
     * @param path represents the path to the model.
     *             May contain "models/item", "item/" part to avoid confusion.
     */
    public ModelItemLocation(String modid, String path) {
        super(modid, path);
    }

    @Override
    @NotNull
    String getPrefix() {
        return "models/item/";
    }

    @Override
    public String toString() {
        return getNamespace() + ":item/" + getPath();
    }
}
