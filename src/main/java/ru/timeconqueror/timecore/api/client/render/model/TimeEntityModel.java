package ru.timeconqueror.timecore.api.client.render.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> {
    private TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
        boxList.addAll(model.boxList);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.translatef(0, 1.501F, 0);//Mojang, WHY???
        model.render(scale);
        GlStateManager.translatef(0, -1.501F, 0);
    }
}