package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModelPart;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TimeModelPart extends ModelRenderer implements ITimeModelPart {
    private final Vector3f scale = new Vector3f(1, 1, 1);
    @Deprecated // use #getOffset
    public Vector3f offset = new Vector3f();
    /**
     * in radians
     */
    private final Vector3f rotation;
    public Vector3f startRotationRadians;
    private final Map<String, TimeModelPart> children;
    public List<TimeModelCube> cubes;

    private MatrixStack.Entry lastTransform = new MatrixStack().last();
    private boolean transformValid;

    public TimeModelPart(int textureWidth, int textureHeight, Vector3f startRotRadians, @NotNull List<TimeModelCube> cubes, Map<String, TimeModelPart> children, boolean neverRender) {
        super(textureWidth, textureHeight, 0, 0);
        this.startRotationRadians = startRotRadians;
        this.rotation = startRotRadians.copy();
        this.visible = !neverRender;
        this.children = children;
        this.cubes = cubes;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        transformValid = true;

        if (this.visible) {
            matrixStackIn.pushPose();

            this.translateAndRotate(matrixStackIn);

            matrixStackIn.scale(scale.x(), scale.y(), scale.z());

            lastTransform = matrixStackIn.last();

            this.compile(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

            for (TimeModelPart part : this.children.values()) {
                part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            matrixStackIn.popPose();
        }
    }

    @Override
    public void translateAndRotate(MatrixStack matrixStackIn) {
        //TODO 1.18+ removal
        setRotations();
        //^

        matrixStackIn.translate(offset.x() / 16F, offset.y() / 16F, offset.z() / 16F);

        //TODO 1.18+ removal -> pull inners here and use rotation instead of xRot, etc.
        super.translateAndRotate(matrixStackIn);
    }

    private void compile(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (TimeModelCube cube : cubes) {
            cube.compile(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    @Override
    public void applyTransform(MatrixStack stack) {
        if (!visible) return;

        if (!transformValid) {
            TimeCore.LOGGER.warn("Method #transformTo was called in an inappropriate time. The part's transform is not calculated yet.", new Exception());

            return;
        }

        MatrixStack.Entry last = stack.last();
        last.pose().set(lastTransform.pose());
        last.normal().load(lastTransform.normal());
    }

    @Override
    public Vector3f getTranslation() {
        return offset;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    @Deprecated // use #getScale().set
    public void setScaleFactor(float scaleX, float scaleY, float scaleZ) {
        this.scale.set(scaleX, scaleY, scaleZ);
    }

    @Deprecated // use getScale
    public Vector3f getScaleFactor() {
        return getScale();
    }

    @Override
    public Map<String, TimeModelPart> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    @Override
    public void reset() {
        transformValid = false;
        rotation.set(startRotationRadians.x(), startRotationRadians.y(), startRotationRadians.z());
        offset.set(0, 0, 0);
        scale.set(1, 1, 1);

        //TODO 1.18+ removal
        setRotations();
    }

    @Deprecated // TODO 1.18 removal
    private void setRotations() {
        this.xRot = rotation.x();
        this.yRot = rotation.y();
        this.zRot = rotation.z();
    }
}
