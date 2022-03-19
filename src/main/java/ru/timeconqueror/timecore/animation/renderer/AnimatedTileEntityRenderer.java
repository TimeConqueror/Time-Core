package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

public abstract class AnimatedTileEntityRenderer<T extends BlockEntity & AnimatedObject<T>> implements BlockEntityRenderer<T> {
    protected TimeModel model;

    public AnimatedTileEntityRenderer(TimeModel model) {
        this.model = model;
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getModel().reset();

        tileEntityIn.getSystem().getAnimationManager().applyAnimations(getModel());

        ResourceLocation texture = getTexture(tileEntityIn);

        RenderType renderType = model.renderType(texture);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5F, 0, 0.5F);

        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(renderType), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

        matrixStackIn.popPose();
    }

    protected abstract ResourceLocation getTexture(T tileEntityIn);

    public ITimeModel getModel() {
        return model;
    }
}
