package ru.timeconqueror.timecore.api.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

public class TimeEntityModel extends ModelBase {
    private TimeModel model;
    private ModelChest chest = new ModelChest();

    public TimeEntityModel(TimeModel model) {
        this.model = model;
        boxList.addAll(model.boxList);
        model.boxList.clear();
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.translate(0, 1.501F, 0);//Mojang, WHY???
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/entity/chest/normal.png"));
        chest.renderAll();
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(TimeCore.MODID, "textures/entity/zombie.png"));
        model.render(scale);
        GlStateManager.translate(0, -1.501F, 0);
    }

    public TimeModel getBaseModel() {
        return model;
    }
}