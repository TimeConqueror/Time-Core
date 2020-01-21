package ru.timeconqueror.timecore.api.client.resource.location;

import org.jetbrains.annotations.NotNull;

public class TextureLocation extends AdaptiveLocation {
    /**
     * @param path represents the path to the texture.
     *             May contain "textures/" part to avoid confusion.
     */
    public TextureLocation(String modid, String path) {
        super(modid, path);
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "textures/";
    }
}
