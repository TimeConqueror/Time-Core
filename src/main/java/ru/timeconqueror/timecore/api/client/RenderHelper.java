package ru.timeconqueror.timecore.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class RenderHelper {
    public static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    /**
     * Binds texture for its later rendering.
     */
    public static void bindTexture(ResourceLocation rl) {
        textureManager.bindTexture(rl);
    }
}
