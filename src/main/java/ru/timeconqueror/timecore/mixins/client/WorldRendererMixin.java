package ru.timeconqueror.timecore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "renderLevel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/shader/ShaderGroup;process(F)V",
                    shift = At.Shift.BEFORE)
    )
    public void preFabulousHellHookBeforeProcess(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (RenderHelper.isFabulousModeEnabled()) {
            StructureRevealer.getInstance().ifPresent(structureRevealer -> structureRevealer.structureRenderer.render(matrixStackIn, true));
        }
    }
}
