package ru.timeconqueror.timecore.api.util.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;

public class RenderHelper {
    private static final Consumer<CompositeState.CompositeStateBuilder> EMPTY_TUNER = builder -> {
    };

    public static Consumer<CompositeState.CompositeStateBuilder> emptyTuner() {
        return EMPTY_TUNER;
    }

    /**
     * Creates render type for drawing in {@link VertexFormat.Mode#QUADS} mode and {@link DefaultVertexFormat#POSITION_TEX} format.
     * Also binds provided texture before rendering and enables blend and alpha.
     *
     * @param texture texture location
     */
    public static RenderType rtPosTexQuads(ResourceLocation texture, ShaderStateShard shader) {
        return RenderHelper.rtTranslucent(TimeCore.rl("textured_rectangles"), VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, texture, shader, RenderHelper.emptyTuner());
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
    public static RenderType rtTranslucent(ResourceLocation name, VertexFormat.Mode mode, VertexFormat format, ResourceLocation texture, ShaderStateShard shader, Consumer<CompositeState.CompositeStateBuilder> builderTuner) {
        Consumer<CompositeState.CompositeStateBuilder> alphaApplier = builder -> builder.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setShaderState(shader);
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
    public static RenderType rtTextured(ResourceLocation name, VertexFormat.Mode mode, VertexFormat format, ResourceLocation texture, Consumer<CompositeState.CompositeStateBuilder> builderTuner) {
        Consumer<CompositeState.CompositeStateBuilder> textureApplier = builder -> builder.setTextureState(new TextureStateShard(texture, false, false));
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
    public static RenderType rt(ResourceLocation name, VertexFormat.Mode mode, VertexFormat format, Consumer<CompositeState.CompositeStateBuilder> builderTuner) {
        CompositeState.CompositeStateBuilder builder = CompositeState.builder();
        builderTuner.accept(builder);

        return RenderType.create(
                name.toString(),
                format,
                mode,
                -1,
                false,
                false,
                builder.createCompositeState(false)
        );
    }

    public static boolean isFabulousModeEnabled() {
        return Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS;
    }
}
