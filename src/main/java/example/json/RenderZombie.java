package example.json;

import example.ModEntities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.TimeEntityRenderer;

import javax.annotation.Nullable;

public class RenderZombie extends TimeEntityRenderer<EntityZombie> {
    public RenderZombie(RenderManager rendermanagerIn) {
        super(rendermanagerIn, ModEntities.zombieModel, 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png");
    }

    @Override
    public float prepareScale(EntityZombie entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(180, 0, (float) entitylivingbaseIn.posY, 0);
        this.preRenderCallback(entitylivingbaseIn, partialTicks);
        return 0.0625F;
    }
}
