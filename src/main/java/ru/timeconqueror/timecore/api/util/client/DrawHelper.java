package ru.timeconqueror.timecore.api.util.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.timecore.api.util.Requirements;

public class DrawHelper {
    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0               start x-coordinate. (x of left-top corner)
     * @param y0               start y-coordinate. (y of left-top corner)
     * @param width            represents both coordinate and texture width along the axis X. For texture it means texture width in parts.
     * @param height           represents both coordinate and texture width along the axis Y. For texture it means texture height in parts.
     * @param zLevel           z-coordinate.
     * @param textureX         index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY         index of start subtexture part on axis Y (y of left-top texture corner).
     * @param texturePartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float texturePartCount) {
        drawTexturedRectByParts(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, width, height, texturePartCount);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0               start x-coordinate. (x of left-top corner)
     * @param y0               start y-coordinate. (y of left-top corner)
     * @param width            Represents coordinate length along the axis X.
     * @param height           Represents coordinate length along the axis Y.
     * @param zLevel           z-coordinate.
     * @param textureX         index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY         index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth     subtexture width in parts.
     * @param textureHeight    subtexture height in parts.
     * @param texturePartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePartCount) {
        float portionFactor = 1 / texturePartCount;
        drawTexturedRect(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionFactor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY            index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth        subtexture width in parts.
     * @param textureHeight       subtexture height in parts.
     * @param textureDivideFactor represents the value equal to 1 / parts. Part count determines in how many parts texture must be divided in both axis. Part description is mentioned above.
     */
    private static void drawTexturedRect(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float textureDivideFactor) {
        Matrix4f pose = matrixStack.last().pose();
        vertexBuilder.vertex(pose, x0, y0, zLevel).uv(textureX * textureDivideFactor, textureY * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0, y0 + height, zLevel).uv(textureX * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0 + height, zLevel).uv((textureX + textureWidth) * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0, zLevel).uv((textureX + textureWidth) * textureDivideFactor, textureY * textureDivideFactor).endVertex();
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0               start x-coordinate. (x of left-top corner)
     * @param y0               start y-coordinate. (y of left-top corner)
     * @param width            represents both coordinate and texture width along the axis X. For texture it means texture width in parts.
     * @param height           represents both coordinate and texture width along the axis Y. For texture it means texture height in parts.
     * @param zLevel           z-coordinate.
     * @param textureX         index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY         index of start subtexture part on axis Y (y of left-top texture corner).
     * @param texturePartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor        color which will be applied to the texture
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float texturePartCount, int argbColor) {
        drawTexturedRectByParts(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, width, height, texturePartCount, argbColor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0               start x-coordinate. (x of left-top corner)
     * @param y0               start y-coordinate. (y of left-top corner)
     * @param width            Represents coordinate length along the axis X.
     * @param height           Represents coordinate length along the axis Y.
     * @param zLevel           z-coordinate.
     * @param textureX         index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY         index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth     subtexture width in parts.
     * @param textureHeight    subtexture height in parts.
     * @param texturePartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor        color which will be applied to the texture
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePartCount, int argbColor) {
        float portionFactor = 1 / texturePartCount;
        drawTexturedRect(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionFactor, argbColor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY            index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth        subtexture width in parts.
     * @param textureHeight       subtexture height in parts.
     * @param textureDivideFactor represents the value equal to 1 / parts. Part count determines in how many parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor           color which will be applied to the texture
     */
    private static void drawTexturedRect(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float textureDivideFactor, int argbColor) {
        Matrix4f pose = matrixStack.last().pose();

        int r = getRed(argbColor);
        int g = getGreen(argbColor);
        int b = getBlue(argbColor);
        int a = getAlpha(argbColor);

        vertexBuilder.vertex(pose, x0, y0, zLevel).color(r, g, b, a).uv(textureX * textureDivideFactor, textureY * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0, y0 + height, zLevel).color(r, g, b, a).uv(textureX * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0 + height, zLevel).color(r, g, b, a).uv((textureX + textureWidth) * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0, zLevel).color(r, g, b, a).uv((textureX + textureWidth) * textureDivideFactor, textureY * textureDivideFactor).endVertex();
    }

    /**
     * Draws textured rectangle with fully bound texture.
     * <p>
     * Required GL Mode: {@link GL11#GL_QUADS}
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     *
     * @param x0     start x-coordinate. (x of left-top corner)
     * @param y0     start y-coordinate. (y of left-top corner)
     * @param width  Represents coordinate length along the axis X.
     * @param height Represents coordinate length along the axis Y.
     * @param zLevel z-coordinate.
     */
    public static void drawTexturedRect(IVertexBuilder builder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel) {
        Matrix4f pose = matrixStack.last().pose();

        builder.vertex(pose, x0, y0, zLevel).uv(0, 0).endVertex();
        builder.vertex(pose, x0, y0 + height, zLevel).uv(0, 1).endVertex();
        builder.vertex(pose, x0 + width, y0 + height, zLevel).uv(1, 1).endVertex();
        builder.vertex(pose, x0 + width, y0, zLevel).uv(1, 0).endVertex();
    }

    /**
     * Draws textured rectangle with autoexpandable width.
     * How it works: this method renders left and right part of rectangle, depending on given {@code requiredWidth}, and then repeats center element until it fill all remaining width.
     * <p>
     * If {@code requiredWidth} is less than the sum of {@code startElement, endElement} width, it will be expanded to this sum.
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0               start x-coordinate. (x of left-top corner)
     * @param y0               start y-coordinate. (y of left-top corner)
     * @param requiredWidth    what coordinate width must rectangle have.
     * @param zLevel           z-coordinate.
     * @param startElement     element, that represents left rectangle part.
     * @param repeatElement    element, that represents repeat rectangle part.
     * @param endElement       element, that represents right rectangle part.
     * @param texturePartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     */
    public static void drawWidthExpandableTexturedRect(IVertexBuilder builder, MatrixStack matrixStack, float x0, float y0, float requiredWidth, float zLevel, TexturedRect startElement, TexturedRect repeatElement, TexturedRect endElement, float texturePartCount) {
        float startWidth = startElement.width;
        float endWidth = endElement.width;
        float minWidth = startWidth + endWidth;

        if (requiredWidth <= minWidth) {
            DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0, y0, startWidth, startElement.height, zLevel, startElement.textureX, startElement.textureY, startElement.textureWidth, startElement.textureHeight, texturePartCount);
            DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0 + startWidth, y0, endWidth, endElement.height, zLevel, endElement.textureX, endElement.textureY, endElement.textureWidth, endElement.textureHeight, texturePartCount);
        } else {
            float remainingWidth = requiredWidth - minWidth;
            float repeatWidth = repeatElement.width;
            float repeatTimes = remainingWidth / repeatWidth;

            int fullTimes = (int) repeatTimes;
            float fracPart = repeatTimes - (int) repeatTimes;

            DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0, y0, startWidth, startElement.height, zLevel, startElement.textureX, startElement.textureY, startElement.textureWidth, startElement.textureHeight, texturePartCount);

            float extraX = startWidth;
            for (int i = 0; i < fullTimes; i++) {
                DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0 + extraX, y0, repeatElement.width, repeatElement.height, zLevel, repeatElement.textureX, repeatElement.textureY, repeatElement.textureWidth, repeatElement.textureHeight, texturePartCount);
                extraX += repeatElement.width;
            }

            DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0 + extraX, y0, repeatWidth * fracPart, repeatElement.height, zLevel, repeatElement.textureX, repeatElement.textureY, repeatElement.textureWidth * fracPart, repeatElement.textureHeight, texturePartCount);
            extraX += repeatWidth * fracPart;

            DrawHelper.drawTexturedRectByParts(builder, matrixStack, x0 + extraX, y0, endWidth, endElement.height, zLevel, endElement.textureX, endElement.textureY, endElement.textureWidth, endElement.textureHeight, texturePartCount);
        }
    }

    /**
     * Returns red channel data of the ARGB color.
     */
    public static int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    /**
     * Returns green channel data of the ARGB color.
     */
    public static int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    /**
     * Returns blue channel data of the ARGB color.
     */
    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    /**
     * Returns alpha channel data of the ARGB color.
     */
    public static int getAlpha(int argb) {
        return argb >> 24 & 0xFF;
    }

    /**
     * Returns the opaque version of this color (without alpha)
     */
    public static int opaquefy(int argb) {
        return argb | 0xFF000000;
    }

    /**
     * Returns color with changed alpha
     *
     * @param alpha should be in range from 0 to 255.
     */
    public static int changeAlpha(int argb, int alpha) {
        Requirements.inRangeInclusive(alpha, 0, 255);
        argb &= 0x00FFFFFF;
        return argb | alpha << 24;
    }

    /**
     * Interpolates the color between a and b.
     *
     * @param a       the first argb color
     * @param b       the second argb color
     * @param percent percentage, from 0 to 1
     */
    public static int interpolateColor(int a, int b, float percent) {
        if (percent <= 0) {
            return a;
        } else if (percent >= 1) {
            return b;
        }

        int ra = (a >> 16) & 0xFF;
        int ga = (a >> 8) & 0xFF;
        int ba = a & 0xFF;
        int aa = a >> 24;

        int rb = (b >> 16) & 0xFF;
        int gb = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int ab = b >> 24;

        float p1 = 1 - percent;

        int rc = (int) (p1 * ra + percent * rb);
        int gc = (int) (p1 * ga + percent * gb);
        int bc = (int) (p1 * ba + percent * bb);
        int ac = (int) (p1 * aa + percent * ab);

        return (ac << 24) + (rc << 16) + (gc << 8) + bc;
    }

    /**
     * Draws string.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawString(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        fontRendererIn.draw(stack, text, x, y, color);
    }

    /**
     * Draws string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawStringWithShadow(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        fontRendererIn.drawShadow(stack, text, x, y, color);
    }

    /**
     * Draws x-centered string.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXCenteredString(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(stack, fontRendererIn, text, x - fontRendererIn.width(text) / 2F, y, color);
    }

    /**
     * Draws x-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXCenteredStringWithShadow(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(stack, fontRendererIn, text, x - fontRendererIn.width(text) / 2F, y, color);
    }

    /**
     * Draws y-centered string.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawYCenteredString(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(stack, fontRendererIn, text, x, y - fontRendererIn.lineHeight / 2F, color);
    }

    /**
     * Draws y-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawYCenteredStringWithShadow(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(stack, fontRendererIn, text, x, y - fontRendererIn.lineHeight / 2F, color);
    }

    /**
     * Draws xy-centered string.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXYCenteredString(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(stack, fontRendererIn, text, x - fontRendererIn.width(text) / 2F, y - fontRendererIn.lineHeight / 2F, color);
    }

    /**
     * Draws xy-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXYCenteredStringWithShadow(MatrixStack stack, FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(stack, fontRendererIn, text, x - fontRendererIn.width(text) / 2F, y - fontRendererIn.lineHeight / 2F, color);
    }

    /**
     * Adds filled bounding box to provider vertex builder.
     * <p>
     * Provided builder should have {@link DefaultVertexFormats#POSITION_COLOR} mode and {@link GL11#GL_QUADS} render type.
     */
    public static void drawFilledBoundingBox(MatrixStack matrixStack, IVertexBuilder builder, AxisAlignedBB bb, int argbColor) {
        float red = getRed(argbColor) / 255F;
        float green = getGreen(argbColor) / 255F;
        float blue = getBlue(argbColor) / 255F;
        float alpha = getAlpha(argbColor) / 255F;

        float minX = (float) bb.minX;
        float minY = (float) bb.minY;
        float minZ = (float) bb.minZ;
        float maxX = (float) bb.maxX;
        float maxY = (float) bb.maxY;
        float maxZ = (float) bb.maxZ;

        Matrix4f matrix = matrixStack.last().pose();

        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1

        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2

        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8

        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6

        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7

        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7
    }

    /**
     * Adds line to renderToBuffer buffer.
     * <p>
     * Provided builder should have {@link DefaultVertexFormats#POSITION_COLOR} mode and {@link GL11#GL_LINES} render type.
     *
     * @param builder builder to which add vertices
     * @param stack   matrix stack
     * @param vec1    start vector
     * @param vec2    end vector
     * @param argb    color
     */
    public static void drawLine(IVertexBuilder builder, MatrixStack stack, Vector3f vec1, Vector3f vec2, int argb) {
        drawLine(builder, stack, vec1.x(), vec1.y(), vec1.z(), vec2.x(), vec2.y(), vec2.z(), argb);
    }

    /**
     * Adds line to renderToBuffer buffer.
     * <p>
     * Provided builder should have {@link DefaultVertexFormats#POSITION_COLOR} mode and {@link GL11#GL_LINES} render type.
     *
     * @param builder builder to which add vertices
     * @param stack   matrix stack
     * @param x0      start x coord
     * @param y0      start y coord
     * @param z0      start z coord
     * @param x1      start x coord
     * @param y1      start y coord
     * @param z1      start z coord
     * @param argb    color
     */
    public static void drawLine(IVertexBuilder builder, MatrixStack stack, float x0, float y0, float z0, float x1, float y1, float z1, int argb) {
        drawLine(builder, stack, x0, y0, z0, x1, y1, z1, getRed(argb), getGreen(argb), getBlue(argb), getAlpha(argb));
    }

    /**
     * Adds line to renderToBuffer buffer.
     * <p>
     * Provided builder should have {@link DefaultVertexFormats#POSITION_COLOR} mode and {@link GL11#GL_LINES} render type.
     *
     * @param builder builder to which add vertices
     * @param stack   matrix stack
     * @param x0      start x coord
     * @param y0      start y coord
     * @param z0      start z coord
     * @param x1      start x coord
     * @param y1      start y coord
     * @param z1      start z coord
     * @param r       red channel color. Range: [0, 255]
     * @param g       green channel color. Range: [0, 255]
     * @param b       blue channel color. Range: [0, 255]
     * @param a       alpha channel color. Range: [0, 255]
     */
    public static void drawLine(IVertexBuilder builder, MatrixStack stack, float x0, float y0, float z0, float x1, float y1, float z1, int r, int g, int b, int a) {
        Matrix4f pose = stack.last().pose();
        builder.vertex(pose, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, a).endVertex();
    }

    public static class TexturedRect {
        /**
         * Represents coordinate length along the axis X.
         */
        private final float width;
        /**
         * Represents coordinate length along the axis Y.
         */
        private final float height;
        /**
         * Start texture x-point (x of left-top texture corner).
         */
        private final float textureX;
        /**
         * Start texture y-point (y of left-top texture corner).
         */
        private final float textureY;
        /**
         * Texture width in points.
         * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
         */
        private final float textureWidth;
        /**
         * Texture height in points.
         * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
         */
        private final float textureHeight;

        public TexturedRect(float width, float height, float textureX, float textureY, float textureWidth, float textureHeight) {
            this.width = width;
            this.height = height;
            this.textureX = textureX;
            this.textureY = textureY;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }
    }
}