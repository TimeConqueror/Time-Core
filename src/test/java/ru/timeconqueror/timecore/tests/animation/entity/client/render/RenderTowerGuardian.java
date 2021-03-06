package ru.timeconqueror.timecore.tests.animation.entity.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.tests.animation.entity.client.Models;
import ru.timeconqueror.timecore.tests.animation.entity.entity.TowerGuardianEntity;

public class RenderTowerGuardian extends AnimatedLivingEntityRenderer<TowerGuardianEntity, TimeEntityModel<TowerGuardianEntity>> {
    public RenderTowerGuardian(EntityRendererManager rendererManager) {
        super(rendererManager, Models.towerGuardian.setScaleMultiplier(1.6F), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TowerGuardianEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/tower_guardian.png");
    }
}
