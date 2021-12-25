package ru.timeconqueror.timecore.client.render.model;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class TimeModelBox {
    public final Vector3f pos1;
    public final Vector3f pos2;
    private final Polygon[] quads;

    /**
     * @param origin  The position of the cube, relative to the entity origin - located at the bottom front left point of the cube.
     * @param size    The cube dimensions (x, y, z).
     * @param uv      The starting point in the texture foo.png (x -> horizontal, y -> vertical) for that cube.
     * @param inflate scale factor /Expands the cube, without expanding the UV mapping - useful for making armor look worn, and not part of the entity.
     */
    public TimeModelBox(Vector3f origin, Vector3f size, Vec2 uv, float inflate, boolean mirror, int textureWidth, int textureHeight) {
        int width = (int) size.x();
        int height = (int) size.y();
        int depth = (int) size.z();

        size.set(size.x() == 0 ? 0.008F : size.x(), size.y() == 0 ? 0.008F : size.y(), size.z() == 0 ? 0.008F : size.z());
        float x1 = origin.x();
        float y1 = origin.y();
        float z1 = origin.z();

        int texU = (int) uv.x;
        int texV = (int) uv.y;

        List<Polygon> quads = new ArrayList<>(6);

        float x2 = x1 + size.x();
        float y2 = y1 + size.y();
        float z2 = z1 + size.z();

        this.pos1 = new Vector3f(x1, y1, z1);
        this.pos2 = new Vector3f(x2, y2, z2);

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

        if (depth != 0 && height != 0) {
            quads.add(new Polygon(new Vertex[]{vertex4, vertex, vertex1, vertex5}, texU + depth + width, texV + depth, texU + depth + width + depth, texV + depth + height, textureWidth, textureHeight, mirror, Direction.EAST));
            quads.add(new Polygon(new Vertex[]{vertex7, vertex3, vertex6, vertex2}, texU, texV + depth, texU + depth, texV + depth + height, textureWidth, textureHeight, mirror, Direction.WEST));
        }

        if (width != 0 && depth != 0) {
            quads.add(new Polygon(new Vertex[]{vertex4, vertex3, vertex7, vertex}, texU + depth, texV, texU + depth + width, texV + depth, textureWidth, textureHeight, mirror, Direction.DOWN));
            quads.add(new Polygon(new Vertex[]{vertex1, vertex2, vertex6, vertex5}, texU + depth + width, texV + depth, texU + depth + width + width, texV, textureWidth, textureHeight, mirror, Direction.UP));
        }

        if (width != 0 && height != 0) {
            quads.add(new Polygon(new Vertex[]{vertex, vertex7, vertex2, vertex1}, texU + depth, texV + depth, texU + depth + width, texV + depth + height, textureWidth, textureHeight, mirror, Direction.NORTH));
            quads.add(new Polygon(new Vertex[]{vertex3, vertex4, vertex5, vertex6}, texU + depth + width + depth, texV + depth, texU + depth + width + depth + width, texV + depth + height, textureWidth, textureHeight, mirror, Direction.SOUTH));
        }

        this.quads = quads.toArray(new Polygon[0]);
    }

    public Polygon[] getQuads() {
        return quads;
    }
}
