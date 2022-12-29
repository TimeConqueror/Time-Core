package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.client.render.model.IModelPuppeteer;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModelRenderer;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

public abstract class AnimatedTileEntityRenderer<T extends TileEntity & AnimatedObject<T>> extends TileEntityRenderer<T> implements ITimeModelRenderer<T> {
    private final ModelPuppeteer<T> puppeteer = new ModelPuppeteer<>();
    protected TimeModel model;

    public AnimatedTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, TimeModel model) {
        super(rendererDispatcherIn);
        this.model = model;
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getTimeModel().reset();

        tileEntityIn.getSystem().getAnimationManager().applyAnimations(getTimeModel());
        puppeteer.processModel(tileEntityIn, model, partialTicks);

        ResourceLocation texture = getTexture(tileEntityIn);
        this.renderer.textureManager.bind(texture);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5F, 0, 0.5F);

        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(texture)), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

        matrixStackIn.popPose();
    }

    protected abstract ResourceLocation getTexture(T tileEntityIn);

    @Override
    public TimeModel getTimeModel() {
        return model;
    }

    @Override
    public IModelPuppeteer<T> getPuppeteer() {
        return puppeteer;
    }

    @Deprecated //use #getTimeModel
    public ITimeModel getModel() {
        return getTimeModel();
    }
}
