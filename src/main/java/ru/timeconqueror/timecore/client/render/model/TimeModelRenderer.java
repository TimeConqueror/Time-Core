package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TimeModelRenderer extends ModelPart {
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public List<TimeModelBox> cubes;
    public Vector3f offset = new Vector3f();
    public Vector3f startRotationRadians;
    private final String name;

    public TimeModelRenderer(TimeModel model, Vector3f rotationAngles, String name, @NotNull List<TimeModelBox> cubes, boolean neverRender) {
        super(model);
        this.name = name;
        startRotationRadians = rotationAngles;
        this.xRot = rotationAngles.x();
        this.yRot = rotationAngles.y();
        this.zRot = rotationAngles.z();
        this.visible = !neverRender;
        this.cubes = cubes;
    }

    @Override
    public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                matrixStackIn.pushPose();

                this.translateAndRotate(matrixStackIn);

                matrixStackIn.scale(scaleFactor.x(), scaleFactor.y(), scaleFactor.z());

                this.compile(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

                for (ModelPart modelrenderer : this.children) {
                    modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }

                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public void translateAndRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(offset.x() * (1 / 16F) * scaleFactor.x(), offset.y() * (1 / 16F) * scaleFactor.y(), offset.z() * (1 / 16F) * scaleFactor.z());

        super.translateAndRotate(matrixStackIn);
    }

    private void compile(PoseStack.Pose matrixEntryIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixEntryIn.pose();
        Matrix3f matrix3f = matrixEntryIn.normal();

        for (TimeModelBox box : this.cubes) {
            for (Polygon quads : box.getQuads()) {
                Vector3f vector3f = quads.normal.copy();
                vector3f.transform(matrix3f);
                float f = vector3f.x();
                float f1 = vector3f.y();
                float f2 = vector3f.z();

                for (int i = 0; i < 4; ++i) {
                    ModelPart.Vertex vertex = quads.vertices[i];
                    float f3 = vertex.pos.x() / 16.0F;
                    float f4 = vertex.pos.y() / 16.0F;
                    float f5 = vertex.pos.z() / 16.0F;
                    Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
                    vector4f.transform(matrix4f);
                    bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, packedOverlayIn, packedLightIn, f, f1, f2);
                }
            }
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
