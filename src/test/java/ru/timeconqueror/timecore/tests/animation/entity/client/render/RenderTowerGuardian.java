package ru.timeconqueror.timecore.tests.animation.entity.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.tests.animation.entity.client.AnimationTestEntityRenderers;
import ru.timeconqueror.timecore.tests.animation.entity.entity.TowerGuardianEntity;

public class RenderTowerGuardian extends AnimatedLivingEntityRenderer<TowerGuardianEntity, TimeEntityModel<TowerGuardianEntity>> {
    public RenderTowerGuardian(EntityRendererProvider.Context context) {
        super(context, new TimeEntityModel<>(ModelConfiguration.builder(AnimationTestEntityRenderers.TOWER_GUARDIAN).build()), 0.5F);
    }

    @Override
    protected void setupAnimations(TowerGuardianEntity animatedObject, PoseStack matrixStackIn, float partialTick) {
        super.setupAnimations(animatedObject, matrixStackIn, partialTick);
        getModel().getRoot().getScale().mul(1.6F, 1.6F, 1.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(TowerGuardianEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/tower_guardian.png");
    }
}
