package example;

import com.timeconqueror.timecore.TimeCore;
import com.timeconqueror.timecore.client.objhandler.ObjRenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPhoenix extends ObjRenderLiving {
    public RenderPhoenix() {
        super(new ModelPhoenix(), 0);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return new ResourceLocation(TimeCore.MODID, "textures/entity/phoenix.png");
    }
}
