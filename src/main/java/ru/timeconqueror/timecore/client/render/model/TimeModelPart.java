package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO add method to control it so nobody hasn't dig into how it works
//TODO name rotation be radians, because for now it's unclear
@OnlyIn(Dist.CLIENT)
public class TimeModelPart extends ModelPart {
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public Vector3f offset = new Vector3f();
    public Vector3f startRotationRadians;
    private final Map<String, TimeModelPart> children;
    private final List<TimeModelCube> cubes;

    private PoseStack.Pose lastTransform = new PoseStack().last();
    private boolean transformValid;

    public TimeModelPart(Vector3f startRotRadians, @NotNull List<TimeModelCube> cubes, Map<String, TimeModelPart> children, boolean neverRender) {
        super(Collections.emptyList(), Collections.emptyMap());
        startRotationRadians = startRotRadians;
        this.xRot = startRotRadians.x();
        this.yRot = startRotRadians.y();
        this.zRot = startRotRadians.z();
        this.visible = !neverRender;
        this.children = children;
        this.cubes = cubes;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        transformValid = true;

        if (this.visible) {
            poseStack.pushPose();

            this.translateAndRotate(poseStack);

            poseStack.scale(scaleFactor.x(), scaleFactor.y(), scaleFactor.z());

            lastTransform = poseStack.last();

            this.compile(poseStack.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

            for (TimeModelPart part : this.children.values()) {
                part.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            poseStack.popPose();
        }
    }

    @Override
    public void translateAndRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(offset.x() * (1 / 16F), offset.y() * (1 / 16F), offset.z() * (1 / 16F));

        super.translateAndRotate(matrixStackIn);
    }

    private void compile(PoseStack.Pose pose, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (TimeModelCube cube : cubes) {
            cube.compile(pose, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    /**
     * Transforms current matrix stack's entry directly to this part.
     * Does NOT require calling the same method from parent parts.
     */
    public void applyTransform(PoseStack stack) {
        if (!visible) return;

        if (!transformValid) {
            TimeCore.LOGGER.warn("Method #transformTo was called in an inappropriate time. The part's transform is not calculated yet.", new Exception());

            return;
        }

        PoseStack.Pose last = stack.last();
        last.pose().load(lastTransform.pose());
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
