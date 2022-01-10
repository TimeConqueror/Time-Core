package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> implements ITimeModel {
    private final TimeModel model;

    public TimeEntityModel(TimeModel model) {
        this.model = model;
    }

    /**
     * Sets custom scale for the model.
     * <p>
     * Should only be called once and before first renderToBuffer frame,
     * otherwise you'll see unexpected renderToBuffer behaviour.
     */
    public TimeEntityModel<T> setScaleMultiplier(float scaleMultiplier) {
        model.setScaleMultiplier(scaleMultiplier);

        return this;
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public TimeModelLocation getLocation() {
        return model.getLocation();
    }

    @Override
    public @Nullable TimeModelPart tryGetPart(String partName) {
        return model.tryGetPart(partName);
    }

    @Override
    public TimeModelPart getPart(String partName) {
        return model.getPart(partName);
    }
}