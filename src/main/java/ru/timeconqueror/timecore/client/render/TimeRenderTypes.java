package ru.timeconqueror.timecore.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class TimeRenderTypes extends RenderType {//FIXME port

    //    protected static final RenderStateShard.AlphaStateShard ALPHA_GREATER_MIN_LIMIT = new RenderStateShard.AlphaStateShard(0.001F);
//
//    public static RenderType getOverlay(boolean disableDepthTest) {
//        return RenderType.create("tc_depth_disableable_overlay",
//                DefaultVertexFormat.POSITION_COLOR,
//                GL11.GL_QUADS,
//                256,
//                false,
//                false,
//                CompositeState.builder()
//                        .setCullState(RenderStateShard.NO_CULL/*doesn't disable cull on RenderWorldLast Event, but allows you to disable it manually before rendering*/)
//                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY/*blending*/)
//                        .setDepthTestState(disableDepthTest ? RenderStateShard.NO_DEPTH_TEST : RenderStateShard.LEQUAL_DEPTH_TEST)/*doesn't disable depth  on RenderWorldLast Event, but allows you to disable it manually before rendering*/
//                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
//                        .setAlphaState(ALPHA_GREATER_MIN_LIMIT)
//                        .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
//                        .setOutputState(RenderStateShard.PARTICLES_TARGET)
//                        .createCompositeState(false));
//    }
//
    public TimeRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }
}
