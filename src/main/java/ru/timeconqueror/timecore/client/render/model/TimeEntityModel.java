package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> {
    private final TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
    }

    /**
     * Sets custom scale for the model.
     * <p>
     * Should only be called once and before first render frame,
     * otherwise you'll see unexpected render behaviour.
     */
    public TimeEntityModel<T> setScaleMultiplier(float scaleMultiplier) {
        model.setScaleMultiplier(scaleMultiplier);

        return this;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        GlStateManager.translatef(0, 1.501F, 0);//Mojang, WHY???
        model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        GlStateManager.translatef(0, -1.501F, 0);
    }

    public TimeModel getBaseModel() {
        return model;
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}