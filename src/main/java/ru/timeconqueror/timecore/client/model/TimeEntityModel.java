package ru.timeconqueror.timecore.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> {
    private TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
        boxList.addAll(model.boxList);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png"));
        GlStateManager.translatef(0, 1.501F, 0);
        model.render(scale);
        GlStateManager.translatef(0, -1.501F, 0);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
    }
}
