package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO add method to control it so nobody hasn't dig into how it works
//TODO name rotation be radians, because for now it's unclear
public class TimeModelPart extends ModelRenderer {
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public Vector3f offset = new Vector3f();
    public Vector3f startRotationRadians;
    private final Map<String, TimeModelPart> children;
    public List<TimeModelCube> cubes;

    private MatrixStack.Entry lastTransform = new MatrixStack().last();
    private boolean transformValid;

    public TimeModelPart(int textureWidth, int textureHeight, Vector3f startRotRadians, @NotNull List<TimeModelCube> cubes, Map<String, TimeModelPart> children, boolean neverRender) {
        super(textureWidth, textureHeight, 0, 0);
        startRotationRadians = startRotRadians;
        this.xRot = startRotRadians.x();
        this.yRot = startRotRadians.y();
        this.zRot = startRotRadians.z();
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

            matrixStackIn.scale(scaleFactor.x(), scaleFactor.y(), scaleFactor.z());

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
        matrixStackIn.translate(offset.x() * (1 / 16F), offset.y() * (1 / 16F), offset.z() * (1 / 16F));

        super.translateAndRotate(matrixStackIn);
    }

    private void compile(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (TimeModelCube cube : cubes) {
            cube.compile(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    /**
     * Transforms current matrix stack's entry directly to this part.
     * Does NOT require calling the same method from parent parts.
     */
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

    public void setScaleFactor(float scaleX, float scaleY, float scaleZ) {
        this.scaleFactor.set(scaleX, scaleY, scaleZ);
    }

    public Vector3f getScaleFactor() {
        return scaleFactor;
    }

    public Map<String, TimeModelPart> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    protected void reset() {
        transformValid = false;
        xRot = startRotationRadians.x();
        yRot = startRotationRadians.y();
        zRot = startRotationRadians.z();

        offset.set(0, 0, 0);
        scaleFactor.set(1, 1, 1);
    }
}
