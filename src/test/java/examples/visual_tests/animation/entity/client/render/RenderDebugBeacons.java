package examples.visual_tests.animation.entity.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import examples.visual_tests.animation.entity.entity.DebugBeaconsEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import ru.timeconqueror.timecore.api.util.client.DebugBeacons;

public class RenderDebugBeacons extends LivingEntityRenderer<DebugBeaconsEntity, EntityModel<DebugBeaconsEntity>> {
    public RenderDebugBeacons(EntityRendererProvider.Context ctx_) {
        super(ctx_, null, 4);
    }

    @Override
    public void render(DebugBeaconsEntity livingEntity_, float float_, float float1_, PoseStack poseStack_, MultiBufferSource multiBufferSource_, int int_) {
        DebugBeacons.draw(multiBufferSource_, poseStack_, new Vec3(0, 0, 0), 0xFFFF00FF);
    }

    @Override
    public ResourceLocation getTextureLocation(DebugBeaconsEntity entity_) {
        return null;
    }
}
