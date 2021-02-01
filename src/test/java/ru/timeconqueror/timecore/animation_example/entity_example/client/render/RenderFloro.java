package ru.timeconqueror.timecore.animation_example.entity_example.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;
import ru.timeconqueror.timecore.animation_example.entity_example.client.Models;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

public class RenderFloro extends AnimatedLivingEntityRenderer<FloroEntity, TimeEntityModel<FloroEntity>> {
    public RenderFloro(EntityRendererManager rendererManager) {
        super(rendererManager, Models.floroModel.setScaleMultiplier(1.6F), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FloroEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
