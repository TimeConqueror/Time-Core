package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.List;

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
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public List<TimeModelRenderer> getPieces() {
        return model.getPieces();
    }

    @Override
    public TimeModelRenderer getPiece(String pieceName) {
        return model.getPiece(pieceName);
    }

    @Override
    public @Nullable TimeModelRenderer tryGetPiece(String pieceName) {
        return model.tryGetPiece(pieceName);
    }
}