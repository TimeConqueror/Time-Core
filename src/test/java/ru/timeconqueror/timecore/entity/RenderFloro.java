package ru.timeconqueror.timecore.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;
import ru.timeconqueror.timecore.api.client.render.TimeEntityRenderer;
import ru.timeconqueror.timecore.registry.TEntities;

import javax.annotation.Nullable;

public class RenderFloro extends TimeEntityRenderer<EntityFloro, TimeEntityModel<EntityFloro>> {
    public RenderFloro(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, TEntities.floroModel, 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFloro entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
