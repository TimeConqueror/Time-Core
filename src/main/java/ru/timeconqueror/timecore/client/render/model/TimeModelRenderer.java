package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TimeModelRenderer extends ModelRenderer {
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public List<TimeModelBox> cubes;
    public Vector3f offset = new Vector3f();
    public Vector3f startRotationAngles;
    private final String name;

    public TimeModelRenderer(TimeModel model, Vector3f rotationAngles, String name, @NotNull List<TimeModelBox> cubes, boolean neverRender) {
        super(model);
        this.name = name;
        startRotationAngles = rotationAngles;
        this.rotateAngleX = rotationAngles.getX();
        this.rotateAngleY = rotationAngles.getY();
        this.rotateAngleZ = rotationAngles.getZ();
        this.showModel = !neverRender;
        this.cubes = cubes;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.showModel) {
            if (!this.cubes.isEmpty() || !this.childModels.isEmpty()) {
                matrixStackIn.push();

                matrixStackIn.translate(offset.getX() * scaleFactor.getX(), offset.getY() * scaleFactor.getY(), offset.getZ() * scaleFactor.getZ());

                this.translateRotate(matrixStackIn);

                matrixStackIn.scale(scaleFactor.getX(), scaleFactor.getY(), scaleFactor.getZ());

                this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

                for (ModelRenderer modelrenderer : this.childModels) {
                    modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }

                matrixStackIn.pop();
            }
        }

        resetData();
    }

    private void doRender(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        Matrix3f matrix3f = matrixEntryIn.getNormal();

        for (TimeModelBox box : this.cubes) {
            for (TexturedQuad quads : box.getQuads()) {
                Vector3f vector3f = quads.normal.copy();
                vector3f.transform(matrix3f);
                float f = vector3f.getX();
                float f1 = vector3f.getY();
                float f2 = vector3f.getZ();

                for (int i = 0; i < 4; ++i) {
                    ModelRenderer.PositionTextureVertex vertex = quads.vertexPositions[i];
                    float f3 = vertex.position.getX() / 16.0F;
                    float f4 = vertex.position.getY() / 16.0F;
                    float f5 = vertex.position.getZ() / 16.0F;
                    Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
                    vector4f.transform(matrix4f);
                    bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, f, f1, f2);
                }
            }
        }
    }

    private void resetData() {
        rotateAngleX = startRotationAngles.getX();
        rotateAngleY = startRotationAngles.getY();
        rotateAngleZ = startRotationAngles.getZ();

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
