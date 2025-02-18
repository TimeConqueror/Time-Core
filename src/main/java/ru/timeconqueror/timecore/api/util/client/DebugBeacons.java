package ru.timeconqueror.timecore.api.util.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class DebugBeacons {
    /**
     * Draws line, which starts from provided vector and goes up to 16 blocks.
     */
    public static void draw(MultiBufferSource bufferIn, PoseStack stack, Vec3 vec, int argb) {
        buildLine(bufferIn.getBuffer(RenderType.lines()), stack, vec, vec.add(0, 16, 0), argb);
    }

    private static void buildLine(VertexConsumer builder, PoseStack stack, Vec3 vec1, Vec3 vec2, int argb) {
        Matrix4f pose = stack.last().pose();

        Vec3 normal = computeNormal(vec1, vec2);

        int r = DrawHelper.getRed(argb);
        int g = DrawHelper.getGreen(argb);
        int b = DrawHelper.getBlue(argb);
        int a = DrawHelper.getAlpha(argb);

        builder.vertex(pose, (float) vec1.x(), (float) vec1.y(), (float) vec1.z())
                .color(r, g, b, a)
                .normal((float) normal.x(), (float) normal.y(), (float) normal.z())
                .endVertex();
        builder.vertex(pose, (float) vec2.x(), (float) vec2.y(), (float) vec2.z())
                .color(r, g, b, a)
                .normal((float) normal.x(), (float) normal.y(), (float) normal.z())
                .endVertex();
    }

    private static Vec3 computeNormal(Vec3 point1, Vec3 point2) {
        Vec3 direction = point2.subtract(point1).normalize();
        Vec3 up = new Vec3(0, 1, 0);

        if (Math.abs(direction.y) > 0.99) {
            up = new Vec3(1, 0, 0);
        }

        return direction.cross(up).normalize();
    }
}
