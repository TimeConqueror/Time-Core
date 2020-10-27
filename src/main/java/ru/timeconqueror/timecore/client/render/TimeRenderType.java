package ru.timeconqueror.timecore.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class TimeRenderType extends RenderType {
    protected static final RenderState.AlphaState ALPHA_GREATER_MIN_LIMIT = new RenderState.AlphaState(0.001F);

    public static RenderType getOverlay(boolean disableDepthTest) {
        return RenderType.create("tc_depth_disableable_overlay",
                DefaultVertexFormats.POSITION_COLOR,
                GL11.GL_QUADS,
                256,
                false,
                false,
                RenderType.State.builder()
                        .setCullState(RenderState.NO_CULL/*doesn't disable cull on RenderWorldLast Event, but allows you to disable it manually before rendering*/)
                        .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY/*blending*/)
                        .setDepthTestState(disableDepthTest ? RenderState.NO_DEPTH_TEST : RenderState.LEQUAL_DEPTH_TEST)/*doesn't disable depth  on RenderWorldLast Event, but allows you to disable it manually before rendering*/
                        .setWriteMaskState(RenderState.COLOR_WRITE)
                        .setAlphaState(ALPHA_GREATER_MIN_LIMIT)
                        .setLayeringState(RenderState.POLYGON_OFFSET_LAYERING)
                        .setOutputState(RenderState.PARTICLES_TARGET)
                        .createCompositeState(false));
    }

    public TimeRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }
}
