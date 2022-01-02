package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class ModelRenderUtils {
    /**
     * Renders model inside {@link BlockEntityRenderer}
     */
    public static void renderInTile(TimeModel model, ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();

        matrixStack.translate(0.5F, 0, 0.5F);

        matrixStack.scale(-1, -1, 1);
        model.renderToBuffer(matrixStack, buffer.getBuffer(model.renderType(texture)), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

        matrixStack.popPose();
    }
}
