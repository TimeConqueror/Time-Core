package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

public abstract class AnimatedTileEntityRenderer<T extends TileEntity & AnimatedObject<T>> extends TileEntityRenderer<T> {
    protected TimeModel model;

    public AnimatedTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, TimeModel model) {
        super(rendererDispatcherIn);
        this.model = model;
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getModel().reset();

        tileEntityIn.getSystem().getAnimationManager().applyAnimations(getModel());

        ResourceLocation texture = getTexture(tileEntityIn);
        this.renderer.textureManager.bind(texture);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5F, 0, 0.5F);

        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(texture)), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

        matrixStackIn.popPose();
    }

    protected abstract ResourceLocation getTexture(T tileEntityIn);

    public ITimeModel getModel() {
        return model;
    }
}
