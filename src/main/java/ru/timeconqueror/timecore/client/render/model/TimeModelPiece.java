package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TimeModelPiece extends ModelPart {
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public List<TimeModelBox> cubes;
    public Vector3f offset = new Vector3f();
    public Vector3f startRotationRadians;
    private final String name;

    public TimeModelPiece(TimeModel model, Vector3f rotationAngles, String name, @NotNull List<TimeModelBox> cubes, boolean neverRender) {
        super(Collections.emptyList(), null/*FIXME PORT*/);
        this.name = name;
        startRotationRadians = rotationAngles;
        this.xRot = rotationAngles.x();
        this.yRot = rotationAngles.y();
        this.zRot = rotationAngles.z();
        this.visible = !neverRender;
        this.cubes = cubes;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                poseStack.pushPose();

                this.translateAndRotate(poseStack);

                poseStack.scale(scaleFactor.x(), scaleFactor.y(), scaleFactor.z());

                this.compile(poseStack.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

                for (ModelPart piece : this.children.values()) {
                    piece.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }

                poseStack.popPose();
            }
        }
    }

    @Override
    public void translateAndRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(offset.x() * (1 / 16F) * scaleFactor.x(), offset.y() * (1 / 16F) * scaleFactor.y(), offset.z() * (1 / 16F) * scaleFactor.z());

        super.translateAndRotate(matrixStackIn);
    }

    private void compile(PoseStack.Pose pose, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        for (TimeModelBox box : this.cubes) {
            box.compile(bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha, matrix4f, matrix3f);
        }
    }

    protected void reset() {
        xRot = startRotationRadians.x();
        yRot = startRotationRadians.y();
        zRot = startRotationRadians.z();

        offset.set(0, 0, 0);
        scaleFactor.set(1, 1, 1);
    }

    public void setScaleFactor(float scaleX, float scaleY, float scaleZ) {
        this.scaleFactor.set(scaleX, scaleY, scaleZ);
    }

    public Vector3f getScaleFactor() {
        return scaleFactor;
    }

    public String getName() {
        return name;
    }
}
