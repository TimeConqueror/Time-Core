package ru.timeconqueror.timecore.animation_example.entity_example.client.render;

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
        super(context, new TimeEntityModel<>(ModelConfiguration.builder(Models.FLORO).scaled(1.6F).build()), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FloroEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
