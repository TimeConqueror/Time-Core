package ru.timeconqueror.timecore.util.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.timecore.api.util.Requirements;

//FIXME change rendering of rectangles, get rid of tessellator and buffer builder
public class DrawHelper {
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder bufferBuilder = tessellator.getBuffer();

    /**
     * Draws textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, width, height} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0          start x-coordinate. (x of left-top corner)
     * @param y0          start y-coordinate. (y of left-top corner)
     * @param width       represents both coordinate and texture width along the axis X. For texture it means texture points. Point description is mentioned above.
     * @param height      represents both coordinate and texture height along the axis Y.  For texture it means texture points. Point description is mentioned above.
     * @param zLevel      z-coordinate.
     * @param textureX    start texture x-point (x of left-top texture corner). Point description is mentioned above.
     * @param textureY    start texture y-point (y of left-top texture corner). Point description is mentioned above.
     * @param pointNumber in how much points texture must be divided. Point description is mentioned above.
     */
    public static void drawTexturedRect(double x0, double y0, float width, float height, double zLevel, float textureX, float textureY, float pointNumber) {
        drawTexturedRect(x0, y0, width, height, zLevel, textureX, textureY, width, height, pointNumber);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Term, used in parameters:
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0            start x-coordinate. (x of left-top corner)
     * @param y0            start y-coordinate. (y of left-top corner)
     * @param width         Represents coordinate length along the axis X.
     * @param height        Represents coordinate length along the axis Y.
     * @param zLevel        z-coordinate.
     * @param textureX      start texture x-point (x of left-top texture corner). Point description is mentioned above.
     * @param textureY      start texture y-point (y of left-top texture corner). Point description is mentioned above.
     * @param textureWidth  texture width in points. Point description is mentioned above.
     * @param textureHeight texture height in points. Point description is mentioned above.
     * @param pointNumber   in how much points texture must be divided. Point description is mentioned above.
     */
    public static void drawTexturedRect(double x0, double y0, double width, double height, double zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float pointNumber) {
        float portionFactor = 1 / pointNumber;
        drawTexturedRectP(x0, y0, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionFactor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            start texture x-point (x of left-top texture corner). Point description is mentioned above.
     * @param textureY            start texture y-point (y of left-top texture corner). Point description is mentioned above.
     * @param textureWidth        texture width in points. Point description is mentioned above.
     * @param textureHeight       texture height in points. Point description is mentioned above.
     * @param texturePointPortion represents the percentage point to whole texture. Equals to 1 / point number.
     */
    public static void drawTexturedRectP(double x0, double y0, double width, double height, double zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePointPortion) {
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(textureX * texturePointPortion, textureY * texturePointPortion).endVertex();
        bufferBuilder.pos(x0, y0 + height, zLevel).tex(textureX * texturePointPortion, (textureY + textureHeight) * texturePointPortion).endVertex();
        bufferBuilder.pos(x0 + width, y0 + height, zLevel).tex((textureX + textureWidth) * texturePointPortion, (textureY + textureHeight) * texturePointPortion).endVertex();
        bufferBuilder.pos(x0 + width, y0, zLevel).tex((textureX + textureWidth) * texturePointPortion, textureY * texturePointPortion).endVertex();
        tessellator.draw();
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, width, height} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               represents both coordinate and texture width along the axis X. For texture it means texture points. Point description is mentioned above.
     * @param height              represents both coordinate and texture height along the axis Y.  For texture it means texture points.  Point description is mentioned above.
     * @param zLevel              z-coordinate.
     * @param textureX            start texture x-point (x of left-top texture corner). Point description is mentioned above.
     * @param textureY            start texture y-point (y of left-top texture corner). Point description is mentioned above.
     * @param texturePointPortion represents the percentage point to whole texture. Equals to 1 / point number.
     */
    public static void drawTexturedRectP(double x0, double y0, float width, float height, double zLevel, float textureX, float textureY, float texturePointPortion) {
        drawTexturedRectP(x0, y0, width, height, zLevel, textureX, textureY, width, height, texturePointPortion);
    }

    /**
     * Draws textured rectangle with autoexpandable width. So if you have texture width, for example, in 30 pixels, while your rectangle have a larger width.
     * How it works: this method renders left and right part of rectangle, depending on given {@code requiredWidth}, and then repeats center element until it fill all remaining width.
     * <p>
     * If {@code requiredWidth} is less than the sum of {@code startElement, endElement} width, it will be expanded to this sum.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code startElement, repeatElement, endElement} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0            start x-coordinate. (x of left-top corner)
     * @param y0            start y-coordinate. (y of left-top corner)
     * @param requiredWidth what coordinate width must rectangle have.
     * @param zLevel        z-coordinate.
     * @param startElement  element, that represents left rectangle part.
     * @param repeatElement element, that represents repeat rectangle part.
     * @param endElement    element, that represents right rectangle part.
     * @param pointNumber   in how much points texture must be divided. Point description is mentioned above.
     */
    public static void drawWidthExpandableTexturedRect(float x0, float y0, float requiredWidth, float zLevel, TexturedRect startElement, TexturedRect repeatElement, TexturedRect endElement, float pointNumber) {
        float startWidth = startElement.width;
        float endWidth = endElement.width;
        float minWidth = startWidth + endWidth;

        if (requiredWidth <= minWidth) {
            DrawHelper.drawTexturedRect(x0, y0, startWidth, startElement.height, zLevel, startElement.textureX, startElement.textureY, startElement.textureWidth, startElement.textureHeight, pointNumber);
            DrawHelper.drawTexturedRect(x0 + startWidth, y0, endWidth, endElement.height, zLevel, endElement.textureX, endElement.textureY, endElement.textureWidth, endElement.textureHeight, pointNumber);
        } else {
            float remainingWidth = requiredWidth - minWidth;
            float repeatWidth = repeatElement.width;
            float repeatTimes = remainingWidth / repeatWidth;

            int fullTimes = (int) repeatTimes;
            float fracPart = repeatTimes - (int) repeatTimes;

            DrawHelper.drawTexturedRect(x0, y0, startWidth, startElement.height, zLevel, startElement.textureX, startElement.textureY, startElement.textureWidth, startElement.textureHeight, pointNumber);

            float extraX = startWidth;
            for (int i = 0; i < fullTimes; i++) {
                DrawHelper.drawTexturedRect(x0 + extraX, y0, repeatElement.width, repeatElement.height, zLevel, repeatElement.textureX, repeatElement.textureY, repeatElement.textureWidth, repeatElement.textureHeight, pointNumber);
                extraX += repeatElement.width;
            }

            DrawHelper.drawTexturedRect(x0 + extraX, y0, repeatWidth * fracPart, repeatElement.height, zLevel, repeatElement.textureX, repeatElement.textureY, repeatElement.textureWidth * fracPart, repeatElement.textureHeight, pointNumber);
            extraX += repeatWidth * fracPart;

            DrawHelper.drawTexturedRect(x0 + extraX, y0, endWidth, endElement.height, zLevel, endElement.textureX, endElement.textureY, endElement.textureWidth, endElement.textureHeight, pointNumber);
        }
    }

    /**
     * Draws textured rectangle with full bound texture.
     *
     * @param x0     start x-coordinate. (x of left-top corner)
     * @param y0     start y-coordinate. (y of left-top corner)
     * @param width  Represents coordinate length along the axis X.
     * @param height Represents coordinate length along the axis Y.
     * @param zLevel z-coordinate.
     */
    public static void drawTexturedRect(double x0, double y0, double width, double height, double zLevel) {
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(0, 0).endVertex();
        bufferBuilder.pos(x0, y0 + height, zLevel).tex(0, 1).endVertex();
        bufferBuilder.pos(x0 + width, y0 + height, zLevel).tex(1, 1).endVertex();
        bufferBuilder.pos(x0 + width, y0, zLevel).tex(1, 0).endVertex();
        tessellator.draw();
    }

    /**
     * Draws string.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        fontRendererIn.drawString(text, x, y, color);
    }

    /**
     * Draws string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawStringWithShadow(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        fontRendererIn.drawStringWithShadow(text, x, y, color);
    }

    /**
     * Draws x-centered string.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXCenteredString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(fontRendererIn, text, x - fontRendererIn.getStringWidth(text) / 2F, y, color);
    }

    /**
     * Draws x-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     start y-coordinate (top)
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXCenteredStringWithShadow(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(fontRendererIn, text, x - fontRendererIn.getStringWidth(text) / 2F, y, color);
    }

    /**
     * Draws y-centered string.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawYCenteredString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(fontRendererIn, text, x, y - fontRendererIn.FONT_HEIGHT / 2F, color);
    }

    /**
     * Draws y-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     start x-coordinate (left)
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawYCenteredStringWithShadow(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(fontRendererIn, text, x, y - fontRendererIn.FONT_HEIGHT / 2F, color);
    }

    /**
     * Draws xy-centered string.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXYCenteredString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawString(fontRendererIn, text, x - fontRendererIn.getStringWidth(text) / 2F, y - fontRendererIn.FONT_HEIGHT / 2F, color);
    }

    /**
     * Draws xy-centered string with shadow.
     *
     * @param text  text to be displayed.
     * @param x     center x-coordinate
     * @param y     center y-coordinate
     * @param color HTML color. Example: 0xFF0000 -> red.
     */
    public static void drawXYCenteredStringWithShadow(FontRenderer fontRendererIn, String text, float x, float y, int color) {
        drawStringWithShadow(fontRendererIn, text, x - fontRendererIn.getStringWidth(text) / 2F, y - fontRendererIn.FONT_HEIGHT / 2F, color);
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
    public static int withChangedAlpha(int argb, int alpha) {
        Requirements.inRangeInclusive(alpha, 0, 255);
        argb &= 0x00FFFFFF;
        return argb | alpha << 24;
    }

    /**
     * Adds filled bounding box to render buffer.
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

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        builder.pos(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.pos(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.pos(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2
        builder.pos(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1

        builder.pos(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.pos(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7
        builder.pos(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6
        builder.pos(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2

        builder.pos(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.pos(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1
        builder.pos(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.pos(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8

        builder.pos(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.pos(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();//1
        builder.pos(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();//2
        builder.pos(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6

        builder.pos(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();//3
        builder.pos(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();//4
        builder.pos(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8
        builder.pos(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7

        builder.pos(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//8
        builder.pos(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();//5
        builder.pos(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();//6
        builder.pos(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();//7
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