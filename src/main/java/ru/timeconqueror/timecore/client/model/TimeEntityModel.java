package ru.timeconqueror.timecore.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> {
    private static final ChestModel chest = new ChestModel();
    private TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
        boxList.addAll(model.boxList);
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("textures/entity/chest/normal.png"));
        chest.renderAll();
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png"));
        model.render(scale);
    }
}
