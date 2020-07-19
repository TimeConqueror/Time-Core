package ru.timeconqueror.timecore.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class TimeRenderType extends RenderType {
    protected static final RenderState.AlphaState ALPHA_GREATER_MIN_LIMIT = new RenderState.AlphaState(0.001F);

    public static RenderType getOverlay(boolean disableDepthTest) {
        return RenderType.makeType("tc_depth_disableable_overlay",
                DefaultVertexFormats.POSITION_COLOR,
                GL11.GL_QUADS,
                256,
                false,
                false,
                RenderType.State.getBuilder()
                        .cull(RenderState.CULL_DISABLED/*doesn't disable cull on RenderWorldLast Event, but allows you to disable it manually before rendering*/)
                        .transparency(RenderState.TRANSLUCENT_TRANSPARENCY/*blending*/)
                        .depthTest(disableDepthTest ? RenderState.DEPTH_ALWAYS : RenderState.DEPTH_LEQUAL)/*doesn't disable depth  on RenderWorldLast Event, but allows you to disable it manually before rendering*/
                        .writeMask(disableDepthTest ? RenderState.COLOR_WRITE : RenderState.COLOR_DEPTH_WRITE)
                        .alpha(ALPHA_GREATER_MIN_LIMIT)
                        .layer(RenderState.POLYGON_OFFSET_LAYERING)
                        .build(false));
    }

    public TimeRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }
}
