package ru.timeconqueror.timecore.api.client.resource.location;

import org.jetbrains.annotations.NotNull;

public class ModelBlockLocation extends ModelLocation {
    /**
     * @param path represents the path to the model.
     *             May contain "models/block", "block/" part to avoid confusion.
     */
    public ModelBlockLocation(String modid, String path) {
        super(modid, path);
    }

    @Override
    @NotNull
    String getPrefix() {
        return "models/block/";
    }

    @Override
    public String toString() {
        return getNamespace() + ":block/" + getPath();
    }
}
