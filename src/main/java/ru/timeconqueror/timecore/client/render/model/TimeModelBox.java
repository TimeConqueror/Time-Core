package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ModelRenderer.PositionTextureVertex;
import net.minecraft.client.renderer.model.ModelRenderer.TexturedQuad;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class TimeModelBox {
    public final Vector3f pos1;
    public final Vector3f pos2;
    private final TexturedQuad[] quads;

    /**
     * @param origin  The position of the cube, relative to the entity origin - located at the bottom front left point of the cube.
     * @param size    The cube dimensions (x, y, z).
     * @param uv      The starting point in the texture foo.png (x -> horizontal, y -> vertical) for that cube.
     * @param inflate scale factor /Expands the cube, without expanding the UV mapping - useful for making armor look worn, and not part of the entity.
     */
    public TimeModelBox(Vector3f origin, Vector3f size, Vec2f uv, float inflate, boolean mirror, int textureWidth, int textureHeight) {
        int width = (int) size.getX();
        int height = (int) size.getY();
        int depth = (int) size.getZ();

        size.set(size.getX() == 0 ? 0.008F : size.getX(), size.getY() == 0 ? 0.008F : size.getY(), size.getZ() == 0 ? 0.008F : size.getZ());
        float x = origin.getX();
        float y = origin.getY();
        float z = origin.getZ();

        int texU = (int) uv.x;
        int texV = (int) uv.y;

        List<TexturedQuad> quads = new ArrayList<>(6);

        this.pos1 = new Vector3f(x, y, z);
        this.pos2 = new Vector3f(x + size.getX(), y + size.getY(), z + size.getZ());

        float f = pos2.getX() + inflate;
        float f1 = pos2.getY() + inflate;
        float f2 = pos2.getZ() + inflate;
        x -= inflate;
        y -= inflate;
        z -= inflate;

        if (mirror) {
            float temp = f;
            f = x;
            x = temp;
        }

        PositionTextureVertex vertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex vertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
        PositionTextureVertex vertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
        PositionTextureVertex vertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
        PositionTextureVertex vertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
        PositionTextureVertex vertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
        PositionTextureVertex vertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
        PositionTextureVertex vertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);

        if (depth != 0 && height != 0) {
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex4, vertex, vertex1, vertex5}, texU + depth + width, texV + depth, texU + depth + width + depth, texV + depth + height, textureWidth, textureHeight, mirror, Direction.EAST));
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex7, vertex3, vertex6, vertex2}, texU, texV + depth, texU + depth, texV + depth + height, textureWidth, textureHeight, mirror, Direction.WEST));
        }

        if (width != 0 && depth != 0) {
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex4, vertex3, vertex7, vertex}, texU + depth, texV, texU + depth + width, texV + depth, textureWidth, textureHeight, mirror, Direction.DOWN));
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex1, vertex2, vertex6, vertex5}, texU + depth + width, texV + depth, texU + depth + width + width, texV, textureWidth, textureHeight, mirror, Direction.UP));
        }

        if (width != 0 && height != 0) {
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex, vertex7, vertex2, vertex1}, texU + depth, texV + depth, texU + depth + width, texV + depth + height, textureWidth, textureHeight, mirror, Direction.NORTH));
            quads.add(new TexturedQuad(new PositionTextureVertex[]{vertex3, vertex4, vertex5, vertex6}, texU + depth + width + depth, texV + depth, texU + depth + width + depth + width, texV + depth + height, textureWidth, textureHeight, mirror, Direction.SOUTH));
        }

        this.quads = quads.toArray(new TexturedQuad[0]);
    }

    public TexturedQuad[] getQuads() {
        return quads;
    }
}
