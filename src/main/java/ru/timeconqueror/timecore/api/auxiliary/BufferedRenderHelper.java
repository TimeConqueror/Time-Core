package ru.timeconqueror.timecore.api.auxiliary;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("DuplicatedCode")
public class BufferedRenderHelper {
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder bufferBuilder = tessellator.getBuffer();

    /**
     * Renders textured rectangle.
     * <p>
     * Term, used in parameters:
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0            start x-coordinate.
     * @param y0            start y-coordinate.
     * @param endX          end x-coordinate.
     * @param endY          end y-coordinate.
     * @param zLevel        z-coordinate.
     * @param textureX      start texture x-point. Point description is mentioned above.
     * @param textureY      start texture y-point. Point description is mentioned above.
     * @param textureWidth  texture width in points. Point description is mentioned above.
     * @param textureHeight texture height in points. Point description is mentioned above.
     * @param pointNumber   in how much points texture must be divided. Point description is mentioned above.
     */
    public static void drawTexturedRectN(double x0, double y0, double endX, double endY, double zLevel, double textureX, double textureY, double textureWidth, double textureHeight, double pointNumber) {
        double portionFactor = 1 / pointNumber;
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(textureX * portionFactor, textureY * portionFactor).endVertex();
        bufferBuilder.pos(x0, endY, zLevel).tex(textureX * portionFactor, (textureY + textureHeight) * portionFactor).endVertex();
        bufferBuilder.pos(endX, endY, zLevel).tex((textureX + textureWidth) * portionFactor, (textureY + textureHeight) * portionFactor).endVertex();
        bufferBuilder.pos(endX, y0, zLevel).tex((textureX + textureWidth) * portionFactor, textureY * portionFactor).endVertex();
        tessellator.draw();
    }

    /**
     * Renders textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, width, height} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0          start x-coordinate.
     * @param y0          start y-coordinate.
     * @param zLevel      z-coordinate.
     * @param textureX    start texture x-point. Point description is mentioned above.
     * @param textureY    start texture y-point. Point description is mentioned above.
     * @param width       represents both coordinate and texture width along the axis X. For texture it means texture points. Point description is mentioned above.
     * @param height      represents both coordinate and texture width along the axis Y.  For texture it means texture points. Point description is mentioned above.
     * @param pointNumber in how much points texture must be divided. Point description is mentioned above.
     */
    public static void drawTexturedRectN(double x0, double y0, double textureX, double textureY, double width, double height, double zLevel, double pointNumber) {
        double portionFactor = 1 / pointNumber;
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(textureX * portionFactor, textureY * portionFactor).endVertex();
        bufferBuilder.pos(x0, y0 + height, zLevel).tex(textureX * portionFactor, (textureY + height) * portionFactor).endVertex();
        bufferBuilder.pos(x0 + width, y0 + height, zLevel).tex((textureX + width) * portionFactor, (textureY + height) * portionFactor).endVertex();
        bufferBuilder.pos(x0 + width, y0, zLevel).tex((textureX + width) * portionFactor, textureY * portionFactor).endVertex();
        tessellator.draw();
    }

    /**
     * Renders textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, textureWidth, textureHeight} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0                  start x-coordinate.
     * @param y0                  start y-coordinate.
     * @param endX                end x-coordinate.
     * @param endY                end y-coordinate.
     * @param zLevel              z-coordinate.
     * @param textureX            start texture x-point. Point description is mentioned above.
     * @param textureY            start texture y-point. Point description is mentioned above.
     * @param textureWidth        texture width in points. Point description is mentioned above.
     * @param textureHeight       texture height in points. Point description is mentioned above.
     * @param texturePointPortion represents the percentage point to whole texture. Equals to 1 / point number.
     */
    public static void drawTexturedRectP(double x0, double y0, double endX, double endY, double zLevel, double textureX, double textureY, double textureWidth, double textureHeight, double texturePointPortion) {
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(textureX * texturePointPortion, textureY * texturePointPortion).endVertex();
        bufferBuilder.pos(x0, endY, zLevel).tex(textureX * texturePointPortion, (textureY + textureHeight) * texturePointPortion).endVertex();
        bufferBuilder.pos(endX, endY, zLevel).tex((textureX + textureWidth) * texturePointPortion, (textureY + textureHeight) * texturePointPortion).endVertex();
        bufferBuilder.pos(endX, y0, zLevel).tex((textureX + textureWidth) * texturePointPortion, textureY * texturePointPortion).endVertex();
        tessellator.draw();
    }

    /**
     * Renders textured rectangle.
     * <p>
     * Terms, used in parameters:
     * Point number represents in how much points texture must be divided.
     * Point is a relative texture coordinate. It is used in {@code textureX, textureY, width, height} to determine its sizes and coordinates relative to the entire texture.
     *
     * @param x0                  start x-coordinate.
     * @param y0                  start y-coordinate.
     * @param zLevel              z-coordinate.
     * @param textureX            start texture x-point. Point description is mentioned above.
     * @param textureY            start texture y-point. Point description is mentioned above.
     * @param width               represents both coordinate and texture width along the axis X. For texture it means texture points. Point description is mentioned above.
     * @param height              represents both coordinate and texture width along the axis Y.  For texture it means texture points.  Point description is mentioned above.
     * @param texturePointPortion represents the percentage point to whole texture. Equals to 1 / point number.
     */
    public static void drawTexturedRectP(double x0, double y0, double textureX, double textureY, double width, double height, double zLevel, double texturePointPortion) {
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x0, y0, zLevel).tex(textureX * texturePointPortion, textureY * texturePointPortion).endVertex();
        bufferBuilder.pos(x0, y0 + height, zLevel).tex(textureX * texturePointPortion, (textureY + height) * texturePointPortion).endVertex();
        bufferBuilder.pos(x0 + width, y0 + height, zLevel).tex((textureX + width) * texturePointPortion, (textureY + height) * texturePointPortion).endVertex();
        bufferBuilder.pos(x0 + width, y0, zLevel).tex((textureX + width) * texturePointPortion, textureY * texturePointPortion).endVertex();
        tessellator.draw();
    }
}
