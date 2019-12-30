package example;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

import javax.annotation.Nullable;

public class RenderPhoenix extends RenderLiving<EntityPhoenix> {
    public RenderPhoenix(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelPhoenix(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPhoenix entity) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/phoenix.png");
    }

    @Override
    public float prepareScale(EntityPhoenix entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(180, 0, (float) entitylivingbaseIn.posY, 0);
        this.preRenderCallback(entitylivingbaseIn, partialTicks);
        return 0.0625F;
    }
}
