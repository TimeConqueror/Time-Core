package com.timeconqueror.timecore.client.objhandler;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public abstract class ObjRenderLiving extends RenderLiving {
    public ObjRenderLiving(ModelBase modelIn, float shadowSize) {
        super(modelIn, shadowSize);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityIn, float partialTicks) {
        GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 24.0F * 0.0625F + 0.0078125F, 0.0F);
        super.preRenderCallback(entityIn, partialTicks);
    }
}
