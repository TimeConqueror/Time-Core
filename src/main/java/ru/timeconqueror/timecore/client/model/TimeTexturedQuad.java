package ru.timeconqueror.timecore.client.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.PositionTextureVertex;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

public class TimeTexturedQuad {
    public final int nVertices;
    public PositionTextureVertex[] vertices;
    private boolean invertNormal;

    public TimeTexturedQuad(PositionTextureVertex[] vertices) {
        this.vertices = vertices;
        this.nVertices = vertices.length;
    }

    public TimeTexturedQuad(PositionTextureVertex[] vertices, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight) {
        this(vertices);
        float f = 0.0F / textureWidth;
        float f1 = 0.0F / textureHeight;
        vertices[0] = vertices[0].setTexturePosition(u2 / textureWidth - f, v1 / textureHeight + f1);
        vertices[1] = vertices[1].setTexturePosition(u1 / textureWidth + f, v1 / textureHeight + f1);
        vertices[2] = vertices[2].setTexturePosition(u1 / textureWidth + f, v2 / textureHeight - f1);
        vertices[3] = vertices[3].setTexturePosition(u2 / textureWidth - f, v2 / textureHeight - f1);
    }

    public void flipFace() {
        PositionTextureVertex[] vertex = new PositionTextureVertex[this.vertices.length];

        for (int i = 0; i < this.vertices.length; ++i) {
            vertex[i] = this.vertices[this.vertices.length - i - 1];
        }

        this.vertices = vertex;
    }

    /**
     * Draw this primitive. This is typically called only once as the generated drawing instructions are saved by the
     * renderer and reused later.
     */
    public void draw(BufferBuilder renderer, float scale) {
        Vec3d vec3d = this.vertices[1].vector3D.subtractReverse(this.vertices[0].vector3D);
        Vec3d vec3d1 = this.vertices[1].vector3D.subtractReverse(this.vertices[2].vector3D);
        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
        float f = (float) vec3d2.x;
        float f1 = (float) vec3d2.y;
        float f2 = (float) vec3d2.z;
        if (this.invertNormal) {
            f = -f;
            f1 = -f1;
            f2 = -f2;
        }

        renderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex vertex = this.vertices[i];
            renderer.pos(vertex.vector3D.x * (double) scale, vertex.vector3D.y * (double) scale, vertex.vector3D.z * (double) scale).tex(vertex.texturePositionX, vertex.texturePositionY).normal(f, f1, f2).endVertex();
        }

        Tessellator.getInstance().draw();
    }
}
