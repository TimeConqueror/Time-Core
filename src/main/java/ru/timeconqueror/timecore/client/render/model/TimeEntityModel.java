package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class TimeEntityModel<T extends Entity> extends EntityModel<T> implements ITimeModel {
    private final TimeModel model;

    public TimeEntityModel(ModelConfiguration config) {
        super(config.renderTypeProvider());
        this.model = new TimeModel(config);
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public TimeModelLocation getLocation() {
        return model.getLocation();
    }

    @Override
    public TimeModelPart getPart(String pieceName) {
        return model.getPart(pieceName);
    }

    @Override
    public @Nullable TimeModelPart tryGetPart(String pieceName) {
        return model.tryGetPart(pieceName);
    }

    @Override
    public TimeModelPart getRoot() {
        return model.getRoot();
    }
}