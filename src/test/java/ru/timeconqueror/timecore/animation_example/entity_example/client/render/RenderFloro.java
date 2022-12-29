package ru.timeconqueror.timecore.animation_example.entity_example.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.animation_example.entity_example.client.Models;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

public class RenderFloro extends AnimatedLivingEntityRenderer<FloroEntity, TimeEntityModel<FloroEntity>> {
    public RenderFloro(EntityRendererProvider.Context context) {
        super(context, new TimeEntityModel<>(ModelConfiguration.builder(Models.FLORO).build()), 0.5F);
    }

    @Override
    protected void setupAnimations(FloroEntity animatedObject, PoseStack matrixStackIn, float partialTick) {
        super.setupAnimations(animatedObject, matrixStackIn, partialTick);
        getModel().getRoot().getScale().mul(1.6F, 1.6F, 1.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(FloroEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
