package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelRenderUtils {
    /**
     * Renders model inside {@link TileEntityRenderer}
     */
    public static void renderInTile(TimeModel model, ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();

        matrixStack.translate(0.5F, 0, 0.5F);

        matrixStack.scale(-1, -1, 1);
        model.renderToBuffer(matrixStack, buffer.getBuffer(model.renderType(texture)), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

        matrixStack.popPose();
    }
}
