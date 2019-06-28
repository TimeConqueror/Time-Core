package com.timeconqueror.timecore.client.objhandler.part;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Face {
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;

    @SideOnly(Side.CLIENT)
    public void render(int glMode, Tessellator tessellator, float scale) {
        if (faceNormal == null) {
            faceNormal = this.calculateFaceNormal();
        }

        boolean hasTexture = (textureCoordinates != null) && (textureCoordinates.length > 0);

        if(glMode < 0){
            glMode = GL11.GL_TRIANGLES;
        }

        tessellator.startDrawing(glMode);


        for (int i = 0; i < vertices.length; ++i) {

            if (hasTexture) {
                tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);
                tessellator.addVertexWithUV(vertices[i].x * (double) scale, vertices[i].y * (double) scale, vertices[i].z * (double) scale,
                        textureCoordinates[i].u, textureCoordinates[i].v);
            } else {
                tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);
                tessellator.addVertex(vertices[i].x * (double) scale, vertices[i].y * (double) scale, vertices[i].z * (double) scale);
            }
        }

        tessellator.draw();
    }

    public Vertex calculateFaceNormal() {
        Vec3 v1 = Vec3.createVectorHelper(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3 v2 = Vec3.createVectorHelper(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);

        Vec3 normalVector = v1.crossProduct(v2);
        normalVector.normalize();

        return new Vertex((float) normalVector.xCoord, (float) normalVector.yCoord, (float) normalVector.zCoord);
    }
}
