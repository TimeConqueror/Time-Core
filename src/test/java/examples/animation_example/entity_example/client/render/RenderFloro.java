package examples.animation_example.entity_example.client.render;

import examples.animation_example.entity_example.client.Models;
import examples.animation_example.entity_example.entity.FloroEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.processor.ScaleProcessor;

public class RenderFloro extends AnimatedLivingEntityRenderer<FloroEntity, TimeEntityModel<FloroEntity>> {
    public RenderFloro(EntityRendererProvider.Context ctx) {
        super(ctx, new TimeEntityModel<>(ModelConfiguration.builder(Models.FLORO).build()), 0.5F);
        getPuppeteer().addModelProcessor(new ScaleProcessor<>(1.6F));
    }

    @Override
    public ResourceLocation getTextureLocation(FloroEntity entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/floro.png");
    }
}
