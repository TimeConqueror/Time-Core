package ru.timeconqueror.timecore.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.model.TimeEntityRenderer;
import ru.timeconqueror.timecore.registry.EntityCommonRegistryExample;

public class RenderFloro extends TimeEntityRenderer<EntityFloro, TimeEntityModel<EntityFloro>> {
    public RenderFloro(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, EntityCommonRegistryExample.floroModel, 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityFloro entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
