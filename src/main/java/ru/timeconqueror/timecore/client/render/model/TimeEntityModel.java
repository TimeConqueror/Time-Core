package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> {
    private final TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
        boxList.addAll(model.boxList);
        model.boxList.clear();
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

    /**
     * Renders model with provided scale.
     *
     * @param initialScale controls initial scale settings of the model.
     *                     Once you provided some number as initial scale,
     *                     you should always provide this particular number,
     *                     otherwise you'll see unexpected render behaviour.
     */
    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float initialScale) {
        GlStateManager.translatef(0, 1.501F, 0);//Mojang, WHY???
        model.render(initialScale);
        GlStateManager.translatef(0, -1.501F, 0);
    }

    public TimeModel getBaseModel() {
        return model;
    }
}