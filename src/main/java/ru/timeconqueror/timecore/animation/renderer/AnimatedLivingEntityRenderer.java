package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

public abstract class AnimatedLivingEntityRenderer<T extends LivingEntity & AnimatedObject<T>, M extends TimeEntityModel<T>> extends LivingEntityRenderer<T, M> {
    public AnimatedLivingEntityRenderer(EntityRendererProvider.Context ctx, M model, float shadowSizeIn) {
        super(ctx, model, shadowSizeIn);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        getModel().reset();

        entity.getSystem().getAnimationManager().applyAnimations(getModel());
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    /**
     * Method, which can be used to apply some manually handled transformation.
     * Be attentive, the animation transformation is already applied on parts at this point.
     * It's advisable to use math operations on part's transformation instead of overwriting it,
     * because the second one may have unsuspected behaviour.
     */
    protected void setupAnimations(T animatedObject, PoseStack matrixStackIn, float partialTick) {

    }

    @Override
    protected void setupRotations(T entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F); // to mirror models to a normal state
        setupAnimations(entityLiving, matrixStackIn, partialTicks);
    }

    @Override
    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        super.scale(entitylivingbaseIn, matrixStackIn, partialTickTime);

        matrixStackIn.translate(0, 1.501F, 0);//Mojang, WTF???
    }

    //copy from MobRenderer to prevent default name showing
    protected boolean shouldShowName(T entityIn) {
        return super.shouldShowName(entityIn) && (entityIn.shouldShowName() || entityIn.hasCustomName() && entityIn == this.entityRenderDispatcher.crosshairPickEntity);
    }
}
