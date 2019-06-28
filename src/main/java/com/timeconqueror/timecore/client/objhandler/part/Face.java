package com.timeconqueror.timecore.client.objhandler.part;

import com.sun.javafx.geom.Vec3f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class Face {
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;

    @SideOnly(Side.CLIENT)
    public void render(int glMode, BufferBuilder buffer, float scale) {
        if (faceNormal == null) {
            faceNormal = this.calculateFaceNormal();
        }

        boolean hasTexture = (textureCoordinates != null) && (textureCoordinates.length > 0);

        if(glMode < 0){
            glMode = GL11.GL_TRIANGLES;
        }

        if(hasTexture){
            buffer.begin(glMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
        } else {
            buffer.begin(glMode, DefaultVertexFormats.POSITION_NORMAL);
        }


        for (int i = 0; i < vertices.length; ++i) {

            if (hasTexture) {
                buffer.pos(vertices[i].x* (double)scale,  vertices[i].y* (double)scale, vertices[i].z* (double)scale)
                        .tex(textureCoordinates[i].u,  textureCoordinates[i].v)
                        .normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
            } else {
                buffer.pos(vertices[i].x* (double)scale, vertices[i].y* (double)scale, vertices[i].z* (double)scale).normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
            }
        }

        Tessellator.getInstance().draw();
    }

    public Vertex calculateFaceNormal() {
        Vec3f v1 = new Vec3f(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3f v2 = new Vec3f(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);

        Vec3f normalVector = new Vec3f();
        normalVector.cross(v1, v2);
        normalVector.normalize();

        return new Vertex(normalVector.x, normalVector.y, normalVector.z);
    }
}
