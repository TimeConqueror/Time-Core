package ru.timeconqueror.timecore.client.render.model.loading;

public class MaterialDefinition {
    private final int textureWidth;
    private final int textureHeight;

    public MaterialDefinition(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public int getTextureWidth() {
        return textureWidth;
    }
}