package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.core.Direction;
import ru.timeconqueror.timecore.client.render.model.uv.UVResolver;

import java.util.ArrayList;
import java.util.List;

public class TimeModelCube {
    private final Polygon[] polygons;

    private TimeModelCube(Polygon[] polygons) {
        this.polygons = polygons;
    }

    public static TimeModelCube make(Vector3f origin, Vector3f size, UVResolver uvResolver, float inflate, boolean mirror, int textureWidth, int textureHeight) {
        float width = size.x();
        float height = size.y();
        float depth = size.z();

        float x1 = origin.x();
        float y1 = origin.y();
        float z1 = origin.z();

        List<Polygon> quads = new ArrayList<>(6);

        float x2 = x1 + Math.max(width, 0.008F);
        float y2 = y1 + Math.max(height, 0.008F);
        float z2 = z1 + Math.max(depth, 0.008F);

        x2 += inflate;
        y2 += inflate;
        z2 += inflate;
        x1 -= inflate;
        y1 -= inflate;
        z1 -= inflate;

        if (mirror) {
            float temp = x2;
            x2 = x1;
            x1 = temp;
        }

        Vertex vertex7 = new Vertex(x1, y1, z1, 0.0F, 0.0F);
        Vertex vertex = new Vertex(x2, y1, z1, 0.0F, 8.0F);
        Vertex vertex1 = new Vertex(x2, y2, z1, 8.0F, 8.0F);
        Vertex vertex2 = new Vertex(x1, y2, z1, 8.0F, 0.0F);
        Vertex vertex3 = new Vertex(x1, y1, z2, 0.0F, 0.0F);
        Vertex vertex4 = new Vertex(x2, y1, z2, 0.0F, 8.0F);
        Vertex vertex5 = new Vertex(x2, y2, z2, 8.0F, 8.0F);
        Vertex vertex6 = new Vertex(x1, y2, z2, 8.0F, 0.0F);

        UVResolver.SizedUV sizedUV;
        if (depth != 0 && height != 0) {
            sizedUV = uvResolver.get(Direction.EAST);
            quads.add(makeQuad(vertex4, vertex, vertex1, vertex5, sizedUV, textureWidth, textureHeight, mirror, Direction.EAST));
            sizedUV = uvResolver.get(Direction.WEST);
            quads.add(makeQuad(vertex7, vertex3, vertex6, vertex2, sizedUV, textureWidth, textureHeight, mirror, Direction.WEST));
        }

        if (width != 0 && depth != 0) {
            sizedUV = uvResolver.get(Direction.DOWN);
            quads.add(makeQuad(vertex4, vertex3, vertex7, vertex, sizedUV, textureWidth, textureHeight, mirror, Direction.DOWN));
            sizedUV = uvResolver.get(Direction.UP);
            quads.add(makeQuad(vertex1, vertex2, vertex6, vertex5, sizedUV, textureWidth, textureHeight, mirror, Direction.UP));
        }

        if (width != 0 && height != 0) {
            sizedUV = uvResolver.get(Direction.NORTH);
            quads.add(makeQuad(vertex, vertex7, vertex2, vertex1, sizedUV, textureWidth, textureHeight, mirror, Direction.NORTH));
            sizedUV = uvResolver.get(Direction.SOUTH);
            quads.add(makeQuad(vertex3, vertex4, vertex5, vertex6, sizedUV, textureWidth, textureHeight, mirror, Direction.SOUTH));
        }

        return new TimeModelCube(quads.toArray(new Polygon[0]));
    }

    private static Polygon makeQuad(Vertex vertex1, Vertex vertex2, Vertex vertex3, Vertex vertex4, UVResolver.SizedUV uv, int textureWidth, int textureHeight, boolean mirror, Direction direction) {
        return new Polygon(new Vertex[]{vertex1, vertex2, vertex3, vertex4}, uv.u1(), uv.v1(), uv.u2(), uv.v2(), textureWidth, textureHeight, mirror, direction);
    }

    public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        for (Polygon polygon : polygons) {
            Vector3f normal = polygon.normal.copy();
            normal.transform(matrix3f);

            for (Vertex vertex : polygon.vertices) {
                float x = vertex.pos.x() / 16.0F;
                float y = vertex.pos.y() / 16.0F;
                float z = vertex.pos.z() / 16.0F;
                Vector4f pos = new Vector4f(x, y, z, 1.0F);
                pos.transform(matrix4f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), red, green, blue, alpha, vertex.u, vertex.v, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
