package ru.timeconqueror.timecore.mixins.client;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
//    @Inject(method = "renderLevel",//FIXME PORT?
//            slice = @Slice(
//                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")),
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/client/shader/ShaderGroup;process(F)V",
//                    shift = At.Shift.BEFORE)
//    )
//    public void preFabulousHellHookBeforeProcess(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
//        if (RenderHelper.isFabulousModeEnabled()) {
//            StructureRevealer.getInstance().ifPresent(structureRevealer -> structureRevealer.structureRenderer.render(matrixStackIn, true));
//        }
//    }
}
