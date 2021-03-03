package ru.timeconqueror.timecore.devtools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

public class DebugRenderer {
    /**
     * Draws line, which starts from provided vector and goes up to 16 blocks.
     */
    public static void drawLine(IRenderTypeBuffer bufferIn, MatrixStack stack, Vector3f vec, int argb) {
        DrawHelper.drawLine(bufferIn.getBuffer(RenderType.lines()), stack, vec.x(), vec.y(), vec.z(), vec.x(), vec.y() + 16, vec.z(), argb);
    }

    /**
     * Draws line, which starts from provided vector and goes up to 16 blocks.
     */
    public static void drawLine(IRenderTypeBuffer bufferIn, MatrixStack stack, Vector3d vec, int argb) {
        DrawHelper.drawLine(bufferIn.getBuffer(RenderType.lines()), stack, (float) vec.x(), (float) vec.y(), (float) vec.z(), (float) vec.x(), (float) vec.y() + 16, (float) vec.z(), argb);
    }

    /**
     * Draws line, which starts from provided vector and goes up to 16 blocks.
     */
    public static void drawLine(IRenderTypeBuffer bufferIn, MatrixStack stack, Vector3i vec, int argb) {
        DrawHelper.drawLine(bufferIn.getBuffer(RenderType.lines()), stack, vec.getX(), vec.getY(), vec.getZ(), vec.getX(), vec.getY() + 16, vec.getZ(), argb);
    }
}
