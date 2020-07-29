package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

public abstract class TimeEntityRenderer<T extends LivingEntity & AnimationProvider<T>, M extends TimeEntityModel<T>> extends LivingRenderer<T, M> {
    public TimeEntityRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        entity.getActionManager().getAnimationManager().applyAnimations(getEntityModel());
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);

        matrixStackIn.translate(0, 1.501F, 0);//Mojang, WTF???
    }
}
