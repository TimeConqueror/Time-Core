package example.json;

import example.ModEntities;
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
}
