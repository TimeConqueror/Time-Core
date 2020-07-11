package ru.timeconqueror.timecore.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.model.TimeEntityRenderer;
import ru.timeconqueror.timecore.registry.EntityCommonRegistryExample;

import javax.annotation.Nullable;

public class RenderZombie extends TimeEntityRenderer<EntityZombie, TimeEntityModel<EntityZombie>> {
    public RenderZombie(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, EntityCommonRegistryExample.zombieModel, 0.5F);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityZombie entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png");
    }
}
