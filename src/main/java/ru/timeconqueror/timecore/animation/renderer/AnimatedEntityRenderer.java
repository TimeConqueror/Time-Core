package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

@OnlyIn(Dist.CLIENT)
public abstract class AnimatedEntityRenderer<T extends Entity & AnimatedObject<T>, M extends TimeEntityModel<T>> extends EntityRenderer<T> {
    protected M entityModel;

    public AnimatedEntityRenderer(EntityRendererManager rendererManager, M entityModelIn) {
        super(rendererManager);
        this.entityModel = entityModelIn;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        entity.getSystem().getAnimationManager().applyAnimations(getEntityModel().getBaseModel());

        preRenderCallback(entity, matrixStack, partialTicks);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void preRenderCallback(T entity, MatrixStack matrixStackIn, float partialTicks) {
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F); // to mirror models to a normal state
    }

    public M getEntityModel() {
        return entityModel;
    }
}
