package ru.timeconqueror.timecore.api.auxiliary.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientUtils {
    public static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    /**
     * Binds texture for its later rendering.
     */
    public static void bindTexture(ResourceLocation rl) {
        textureManager.bindTexture(rl);
    }
}
