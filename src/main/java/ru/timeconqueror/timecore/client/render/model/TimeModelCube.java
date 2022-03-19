package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.Direction;
import ru.timeconqueror.timecore.client.render.model.uv.UVResolver;

import java.util.ArrayList;
import java.util.List;

public class TimeModelCube {
    private final TimeQuad[] quads;

    public static TimeModelCube make(Vector3f origin, Vector3f size, UVResolver uvResolver, float inflate, boolean mirror, int textureWidth, int textureHeight) {
        float width = size.x();
        float height = size.y();
        float depth = size.z();

        float x1 = origin.x();
        float y1 = origin.y();
        float z1 = origin.z();

        List<TimeQuad> quads = new ArrayList<>(6);

        float x2 = x1 + Math.max(width, 0.008F);
        float y2 = y1 + Math.max(height, 0.008F);
        float z2 = z1 + Math.max(depth, 0.008F);

        x2 += inflate;
        y2 += inflate;
        z2 += inflate;
        x1 -= inflate;
        y1 -= inflate;
        z1 -= inflate;

//        if (mirror) {//FIXME CHECK MIRRORING
//            float temp = x2;
//            x2 = x1;
//            x1 = temp;
//        }

        Vector3f v1 = new Vector3f(x1, y1, z1);
        Vector3f v2 = new Vector3f(x2, y1, z1);
        Vector3f v3 = new Vector3f(x2, y2, z1);
        Vector3f v4 = new Vector3f(x1, y2, z1);
        Vector3f v5 = new Vector3f(x1, y1, z2);
        Vector3f v6 = new Vector3f(x1, y2, z2);
        Vector3f v7 = new Vector3f(x2, y2, z2);
        Vector3f v8 = new Vector3f(x2, y1, z2);

        if (width != 0 && height != 0) {
            quads.add(makeQuad(v3, v2, v1, v4, uvResolver, textureWidth, textureHeight, mirror, Direction.NORTH));
            quads.add(makeQuad(v6, v5, v8, v7, uvResolver, textureWidth, textureHeight, mirror, Direction.SOUTH));
        }
        if (depth != 0 && height != 0) {
            quads.add(makeQuad(v4, v1, v5, v6, uvResolver, textureWidth, textureHeight, mirror, Direction.WEST));
            quads.add(makeQuad(v7, v8, v2, v3, uvResolver, textureWidth, textureHeight, mirror, Direction.EAST));
        }
        if (width != 0 && depth != 0) {
            quads.add(makeQuad(v2, v8, v5, v1, uvResolver, textureWidth, textureHeight, mirror, Direction.DOWN));
            quads.add(makeQuad(v7, v3, v4, v6, uvResolver, textureWidth, textureHeight, mirror, Direction.UP));
        }

        return new TimeModelCube(quads.toArray(new TimeQuad[0]));
    }

    private static TimeQuad makeQuad(Vector3f pos4, Vector3f pos1, Vector3f pos2, Vector3f pos3, UVResolver uvResolver, int textureWidth, int textureHeight, boolean mirror, Direction direction) {
        UVResolver.SizedUV uv = uvResolver.get(direction);

        float u1 = uv.u1() / (float) textureWidth;
        float u2 = uv.u2() / (float) textureWidth;
        float v1 = uv.v1() / (float) textureHeight;
        float v2 = uv.v2() / (float) textureHeight;

        return new TimeQuad(new TimeVertex[]{
                makeVertex(pos4, u1, v1),
                makeVertex(pos1, u1, v2),
                makeVertex(pos2, u2, v2),
                makeVertex(pos3, u2, v1),
        },
                mirror, direction);
    }

    private static TimeVertex makeVertex(Vector3f pos, float u, float v) {
        return new TimeVertex(pos, u, v);
    }

//    public static TimeModelCube make1(Vector3f origin, Vector3f size, UVResolver uvResolver, float inflate, boolean mirror, int textureWidth, int textureHeight) {
//        float width = size.x();
//        float height = size.y();
//        float depth = size.z();
//
//        float x1 = origin.x();
//        float y1 = origin.y();
//        float z1 = origin.z();
//
//        List<Polygon> quads = new ArrayList<>(6);
//
//        float x2 = x1 + Math.max(width, 0.008F);
//        float y2 = y1 + Math.max(height, 0.008F);
//        float z2 = z1 + Math.max(depth, 0.008F);
//
//        x2 += inflate;
//        y2 += inflate;
//        z2 += inflate;
//        x1 -= inflate;
//        y1 -= inflate;
//        z1 -= inflate;
//
//        if (mirror) {
//            float temp = x2;
//            x2 = x1;
//            x1 = temp;
//        }
//
//        Vertex vertex7 = new Vertex(x1, y1, z1, 0.0F, 0.0F);
//        Vertex vertex = new Vertex(x2, y1, z1, 0.0F, 8.0F);
//        Vertex vertex1 = new Vertex(x2, y2, z1, 8.0F, 8.0F);
//        Vertex vertex2 = new Vertex(x1, y2, z1, 8.0F, 0.0F);
//        Vertex vertex3 = new Vertex(x1, y1, z2, 0.0F, 0.0F);
//        Vertex vertex4 = new Vertex(x2, y1, z2, 0.0F, 8.0F);
//        Vertex vertex5 = new Vertex(x2, y2, z2, 8.0F, 8.0F);
//        Vertex vertex6 = new Vertex(x1, y2, z2, 8.0F, 0.0F);
//
//        UVResolver.SizedUV sizedUV;
//        if (depth != 0 && height != 0) {
//            sizedUV = uvResolver.get(Direction.EAST);
//            quads.add(makeQuad(vertex4, vertex, vertex1, vertex5, sizedUV, textureWidth, textureHeight, mirror, Direction.EAST));
//            sizedUV = uvResolver.get(Direction.WEST);
//            quads.add(makeQuad(vertex7, vertex3, vertex6, vertex2, sizedUV, textureWidth, textureHeight, mirror, Direction.WEST));
//        }
//
//        if (width != 0 && depth != 0) {
//            sizedUV = uvResolver.get(Direction.DOWN);
//            quads.add(makeQuad(vertex4, vertex3, vertex7, vertex, sizedUV, textureWidth, textureHeight, mirror, Direction.DOWN));
//            sizedUV = uvResolver.get(Direction.UP);
//            quads.add(makeQuad(vertex1, vertex2, vertex6, vertex5, sizedUV, textureWidth, textureHeight, mirror, Direction.UP));
//        }
//
//        if (width != 0 && height != 0) {
//            sizedUV = uvResolver.get(Direction.NORTH);
//            quads.add(makeQuad(vertex, vertex7, vertex2, vertex1, sizedUV, textureWidth, textureHeight, mirror, Direction.NORTH));
//            sizedUV = uvResolver.get(Direction.SOUTH);
//            quads.add(makeQuad(vertex3, vertex4, vertex5, vertex6, sizedUV, textureWidth, textureHeight, mirror, Direction.SOUTH));
//        }
//
//        return new TimeModelCube(quads.toArray(new Polygon[0]));
//    }

//    private static Polygon makeQuad(Vertex vertex1, Vertex vertex2, Vertex vertex3, Vertex vertex4, UVResolver.SizedUV uv, int textureWidth, int textureHeight, boolean mirror, Direction direction) {
//        return new Polygon(new Vertex[]{vertex1, vertex2, vertex3, vertex4}, uv.u1(), uv.v1(), uv.u2(), uv.v2(), textureWidth, textureHeight, mirror, direction);
//    }
//
//    public static TimeModelCube faceDependent(Vector3f origin, Vector3f size, UVDefinition.PerFace uv, float inflate, boolean mirror, int textureWidth, int textureHeight) {
//        float width = size.x();
//        float height = size.y();
//        float depth = size.z();
//
//        float x1 = origin.x();
//        float y1 = origin.y();
//        float z1 = origin.z();
//
//        List<Polygon> quads = new ArrayList<>(6);
//
//        float x2 = x1 + Math.max(width, 0.008F);
//        float y2 = y1 + Math.max(height, 0.008F);
//        float z2 = z1 + Math.max(depth, 0.008F);
//
//        x2 += inflate;
//        y2 += inflate;
//        z2 += inflate;
//        x1 -= inflate;
//        y1 -= inflate;
//        z1 -= inflate;
//
//        if (mirror) {
//            float temp = x2;
//            x2 = x1;
//            x1 = temp;
//        }
//
//        Vertex ver7 = new Vertex(x1, y1, z1, 0.0F, 0.0F);
//        Vertex ver0 = new Vertex(x2, y1, z1, 0.0F, 8.0F);
//        Vertex ver1 = new Vertex(x2, y2, z1, 8.0F, 8.0F);
//        Vertex ver2 = new Vertex(x1, y2, z1, 8.0F, 0.0F);
//        Vertex ver3 = new Vertex(x1, y1, z2, 0.0F, 0.0F);
//        Vertex ver4 = new Vertex(x2, y1, z2, 0.0F, 8.0F);
//        Vertex ver5 = new Vertex(x2, y2, z2, 8.0F, 8.0F);
//        Vertex ver6 = new Vertex(x1, y2, z2, 8.0F, 0.0F);
//
//        EnumMap<Direction, FaceUVDefinition> mappings = uv.getMappings();
//        FaceUVDefinition faceUV;
//        if (depth != 0 && height != 0) {
//            faceUV = mappings.get(Direction.EAST);8 2 3 7
//            quads.add(new Polygon(new Vertex[]{ver4, ver0, ver1, ver5}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.EAST));
//            faceUV = mappings.get(Direction.WEST);
//            quads.add(new Polygon(new Vertex[]{ver7, ver3, ver6, ver2}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.WEST));
//        }
//
//        if (width != 0 && depth != 0) {
//            faceUV = mappings.get(Direction.DOWN);
//            quads.add(new Polygon(new Vertex[]{ver4, ver3, ver7, ver0}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.DOWN));
//            faceUV = mappings.get(Direction.UP);
//            quads.add(new Polygon(new Vertex[]{ver1, ver2, ver6, ver5}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.UP));
//        }
//
//        if (width != 0 && height != 0) {
//            faceUV = mappings.get(Direction.NORTH);
//            quads.add(new Polygon(new Vertex[]{ver0, ver7, ver2, ver1}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.NORTH));
//            faceUV = mappings.get(Direction.SOUTH);
//            quads.add(new Polygon(new Vertex[]{ver3, ver4, ver5, ver6}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.SOUTH));
//        }
//
//        return new TimeModelCube(quads.toArray(new Polygon[0]));
//    }
//
//    public static TimeModelCube faceDependent1(Vector3f origin, Vector3f size, UVDefinition.PerFace uv, float inflate, boolean mirror, int textureWidth, int textureHeight) {
//        float width = size.x();
//        float height = size.y();
//        float depth = size.z();
//
//        float x1 = origin.x();
//        float y1 = origin.y();
//        float z1 = origin.z();
//
//        List<Polygon> quads = new ArrayList<>(6);
//
//        float x2 = x1 + Math.max(width, 0.008F);
//        float y2 = y1 + Math.max(height, 0.008F);
//        float z2 = z1 + Math.max(depth, 0.008F);
//
//        x2 += inflate;
//        y2 += inflate;
//        z2 += inflate;
//        x1 -= inflate;
//        y1 -= inflate;
//        z1 -= inflate;
//
//        if (mirror) {
//            float temp = x2;
//            x2 = x1;
//            x1 = temp;
//        }
//
//        Vertex vertex7 = new Vertex(x1, y1, z1, 0.0F, 0.0F);
//        Vertex vertex = new Vertex(x2, y1, z1, 0.0F, 8.0F);
//        Vertex vertex1 = new Vertex(x2, y2, z1, 8.0F, 8.0F);
//        Vertex vertex2 = new Vertex(x1, y2, z1, 8.0F, 0.0F);
//        Vertex vertex3 = new Vertex(x1, y1, z2, 0.0F, 0.0F);
//        Vertex vertex4 = new Vertex(x2, y1, z2, 0.0F, 8.0F);
//        Vertex vertex5 = new Vertex(x2, y2, z2, 8.0F, 8.0F);
//        Vertex vertex6 = new Vertex(x1, y2, z2, 8.0F, 0.0F);
//
//        EnumMap<Direction, FaceUVDefinition> mappings = uv.getMappings();
//        FaceUVDefinition faceUV;
//        if (depth != 0 && height != 0) {
//            faceUV = mappings.get(Direction.EAST);
//            quads.add(new Polygon(new Vertex[]{vertex4, vertex, vertex1, vertex5}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.EAST));
//            faceUV = mappings.get(Direction.WEST);
//            quads.add(new Polygon(new Vertex[]{vertex7, vertex3, vertex6, vertex2}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.WEST));
//        }
//
//        if (width != 0 && depth != 0) {
//            faceUV = mappings.get(Direction.DOWN);
//            quads.add(new Polygon(new Vertex[]{vertex4, vertex3, vertex7, vertex}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.DOWN));
//            faceUV = mappings.get(Direction.UP);
//            quads.add(new Polygon(new Vertex[]{vertex1, vertex2, vertex6, vertex5}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.UP));
//        }
//
//        if (width != 0 && height != 0) {
//            faceUV = mappings.get(Direction.NORTH);
//            quads.add(new Polygon(new Vertex[]{vertex, vertex7, vertex2, vertex1}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.NORTH));
//            faceUV = mappings.get(Direction.SOUTH);
//            quads.add(new Polygon(new Vertex[]{vertex3, vertex4, vertex5, vertex6}, faceUV.uv().x(), faceUV.uv().y(), faceUV.uv().x() + faceUV.size().x(), faceUV.uv().y() + faceUV.size().y(), textureWidth, textureHeight, mirror, Direction.SOUTH));
//        }
//
//        return new TimeModelCube(quads.toArray(new Polygon[0]));
//    }

    private TimeModelCube(TimeQuad[] quads) {
        this.quads = quads;
    }


//    /**
//     * @param origin  The position of the cube, relative to the entity origin - located at the bottom front left point of the cube.
//     * @param size    The cube dimensions (x, y, z).
//     * @param uv      The starting point in the texture foo.png (x -> horizontal, y -> vertical) for that cube.
//     * @param inflate scale factor /Expands the cube, without expanding the UV mapping - useful for making armor look worn, and not part of the entity.
//     */
//    public TimeModelCube(Vector3f origin, Vector3f size, Vec2i uv, float inflate, boolean mirror, int textureWidth, int textureHeight) {
//        int width = (int) size.x();
//        int height = (int) size.y();
//        int depth = (int) size.z();
//
//        size.set(size.x() == 0 ? 0.008F : size.x(), size.y() == 0 ? 0.008F : size.y(), size.z() == 0 ? 0.008F : size.z());
//        float x1 = origin.x();
//        float y1 = origin.y();
//        float z1 = origin.z();
//
//        List<Polygon> quads = new ArrayList<>(6);
//
//        float x2 = x1 + size.x();
//        float y2 = y1 + size.y();
//        float z2 = z1 + size.z();
//
//        x2 += inflate;
//        y2 += inflate;
//        z2 += inflate;
//        x1 -= inflate;
//        y1 -= inflate;
//        z1 -= inflate;
//
//        if (mirror) {
//            float temp = x2;
//            x2 = x1;
//            x1 = temp;
//        }
//
//        Vertex vertex7 = new Vertex(x1, y1, z1, 0.0F, 0.0F);
//        Vertex vertex = new Vertex(x2, y1, z1, 0.0F, 8.0F);
//        Vertex vertex1 = new Vertex(x2, y2, z1, 8.0F, 8.0F);
//        Vertex vertex2 = new Vertex(x1, y2, z1, 8.0F, 0.0F);
//        Vertex vertex3 = new Vertex(x1, y1, z2, 0.0F, 0.0F);
//        Vertex vertex4 = new Vertex(x2, y1, z2, 0.0F, 8.0F);
//        Vertex vertex5 = new Vertex(x2, y2, z2, 8.0F, 8.0F);
//        Vertex vertex6 = new Vertex(x1, y2, z2, 8.0F, 0.0F);
//
//        if (depth != 0 && height != 0) {
//            quads.add(new Polygon(new Vertex[]{vertex4, vertex, vertex1, vertex5}, uv.x() + depth + width, uv.y() + depth, uv.x() + depth + width + depth, uv.y() + depth + height, textureWidth, textureHeight, mirror, Direction.EAST));
//            quads.add(new Polygon(new Vertex[]{vertex7, vertex3, vertex6, vertex2}, uv.x(), uv.y() + depth, uv.x() + depth, uv.y() + depth + height, textureWidth, textureHeight, mirror, Direction.WEST));
//        }
//
//        if (width != 0 && depth != 0) {
//            quads.add(new Polygon(new Vertex[]{vertex4, vertex3, vertex7, vertex}, uv.x() + depth, uv.y(), uv.x() + depth + width, uv.y() + depth, textureWidth, textureHeight, mirror, Direction.DOWN));
//            quads.add(new Polygon(new Vertex[]{vertex1, vertex2, vertex6, vertex5}, uv.x() + depth + width, uv.y() + depth, uv.x() + depth + width + width, uv.y(), textureWidth, textureHeight, mirror, Direction.UP));
//        }
//
//        if (width != 0 && height != 0) {
//            quads.add(new Polygon(new Vertex[]{vertex, vertex7, vertex2, vertex1}, uv.x() + depth, uv.y() + depth, uv.x() + depth + width, uv.y() + depth + height, textureWidth, textureHeight, mirror, Direction.NORTH));
//            quads.add(new Polygon(new Vertex[]{vertex3, vertex4, vertex5, vertex6}, uv.x() + depth + width + depth, uv.y() + depth, uv.x() + depth + width + depth + width, uv.y() + depth + height, textureWidth, textureHeight, mirror, Direction.SOUTH));
//        }
//
//        this.polygons = quads.toArray(new Polygon[0]);
//    }

    public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        for (TimeQuad quad : quads) {
            Vector3f normal = quad.normal.copy();
            normal.transform(matrix3f);

            for (TimeVertex vertex : quad.vertices) {
                float x = vertex.getPos().x() / 16.0F;
                float y = vertex.getPos().y() / 16.0F;
                float z = vertex.getPos().z() / 16.0F;
                Vector4f pos = new Vector4f(x, y, z, 1.0F);
                pos.transform(matrix4f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), red, green, blue, alpha, vertex.getU(), vertex.getV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
