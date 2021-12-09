package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

public abstract class AnimatedLivingEntityRenderer<T extends LivingEntity & AnimatedObject<T>, M extends TimeEntityModel<T>> extends LivingRenderer<T, M> {
    public AnimatedLivingEntityRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        getModel().reset();

        entity.getSystem().getAnimationManager().applyAnimations(getModel());
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    protected void scale(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        super.scale(entitylivingbaseIn, matrixStackIn, partialTickTime);

        matrixStackIn.translate(0, 1.501F, 0);//Mojang, WTF???
    }

    //copy from MobRenderer to prevent default name showing
    protected boolean shouldShowName(T entityIn) {
        return super.shouldShowName(entityIn) && (entityIn.shouldShowName() || entityIn.hasCustomName() && entityIn == this.entityRenderDispatcher.crosshairPickEntity);
    }
}
