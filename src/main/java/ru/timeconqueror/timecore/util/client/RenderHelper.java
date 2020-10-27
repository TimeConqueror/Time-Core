package ru.timeconqueror.timecore.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.ResourceLocation;

public class RenderHelper {
    public static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    /**
     * Binds texture for its later rendering.
     */
    public static void bind(ResourceLocation rl) {
        textureManager.bind(rl);
    }

    public static boolean isFabulousModeEnabled() {
        return Minecraft.getInstance().options.graphicsMode == GraphicsFanciness.FABULOUS;
    }
}
