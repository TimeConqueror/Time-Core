package ru.timeconqueror.timecore.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.registry.ModEntities;

import javax.annotation.Nullable;

public class RenderZombie extends LivingRenderer<EntityZombie, TimeEntityModel<EntityZombie>> {
    public RenderZombie(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, ModEntities.zombieModel, 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png");
    }
}
