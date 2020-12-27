package ru.timeconqueror.timecore.api.util.client;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;

public class RenderHelper extends RenderType {
    private static final Consumer<State.Builder> EMPTY_TUNER = builder -> {
    };

    /**
     * Creates pipeline which allows to use render types for gui render.
     */
    public static RenderPipeline guiRenderPipeline() {
        return new RenderPipeline();
    }

    public static Consumer<State.Builder> emptyTuner() {
        return EMPTY_TUNER;
    }

    /**
     * Creates render type for drawing in {@link GLDrawMode#QUADS} mode and {@link DefaultVertexFormats#POSITION_TEX} format.
     * Also binds provided texture before rendering and enables blend and alpha.
     *
     * @param texture texture location
     */
    public static RenderType rtTexturedRectangles(ResourceLocation texture) {
        return RenderHelper.rtTexturedAlphaSupport(TimeCore.rl("textured_rectangles"), GLDrawMode.QUADS, DefaultVertexFormats.POSITION_TEX, texture, RenderHelper.emptyTuner());
    }

    /**
     * Creates render type, which binds provided texture before rendering and enables blend and alpha.
     *
     * @param name         name of render type
     * @param mode         draw mode
     * @param format       draw format
     * @param texture      texture location
     * @param builderTuner tuner for applying extra settings
     */
    public static RenderType rtTexturedAlphaSupport(ResourceLocation name, GLDrawMode mode, VertexFormat format, ResourceLocation texture, Consumer<State.Builder> builderTuner) {
        Consumer<RenderType.State.Builder> alphaApplier = builder -> builder.setAlphaState(AlphaState.DEFAULT_ALPHA).setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY);
        return rtTextured(name, mode, format, texture, builderTuner.andThen(alphaApplier));
    }

    /**
     * Creates render type, which binds provided texture before rendering.
     *
     * @param name         name of render type
     * @param mode         draw mode
     * @param format       draw format
     * @param texture      texture location
     * @param builderTuner tuner for applying extra settings
     */
    public static RenderType rtTextured(ResourceLocation name, GLDrawMode mode, VertexFormat format, ResourceLocation texture, Consumer<State.Builder> builderTuner) {
        Consumer<RenderType.State.Builder> textureApplier = builder -> builder.setTextureState(new TextureState(texture, false, false));
        return rt(name, mode, format, builderTuner.andThen(textureApplier));
    }

    /**
     * Utility method for creating render types.
     *
     * @param name         name of render type
     * @param mode         draw mode
     * @param format       draw format
     * @param builderTuner tuner for applying extra settings
     */
    public static RenderType rt(ResourceLocation name, GLDrawMode mode, VertexFormat format, Consumer<RenderType.State.Builder> builderTuner) {
        State.Builder builder = State.builder();
        builderTuner.accept(builder);

        return RenderType.create(
                name.toString(),
                format,
                mode.getMode(),
                -1,
                false,
                false,
                builder.createCompositeState(false)
        );
    }

    public static boolean isFabulousModeEnabled() {
        return Minecraft.getInstance().options.graphicsMode == GraphicsFanciness.FABULOUS;
    }

    public static class RenderPipeline {
        private final IRenderTypeBuffer.Impl buffer;

        private RenderPipeline() {
            buffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        }

        public void render(RenderType renderType, Consumer<IVertexBuilder> renderer) {
            IVertexBuilder vertexBuilder = this.buffer.getBuffer(renderType);
            renderer.accept(vertexBuilder);
        }

        public void renderAndEnd(RenderType renderType, Consumer<IVertexBuilder> renderer) {
            render(renderType, renderer);
            buffer.endBatch(renderType);
        }

        public void end() {
            buffer.endBatch();
        }
    }

    protected RenderHelper(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }
}
