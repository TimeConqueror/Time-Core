package ru.timeconqueror.timecore.mod.mixins.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.devtools.StructureRevealer;
import ru.timeconqueror.timecore.util.client.RenderHelper;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "renderLevel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")),
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/shader/ShaderGroup;process(F)V",
                    shift = At.Shift.BEFORE)
    )
    public void preFabulousHellHookBeforeProcess(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (RenderHelper.isFabulousModeEnabled()) {
            StructureRevealer.getInstance().ifPresent(structureRevealer -> structureRevealer.structureRenderer.render(matrixStackIn, true));
        }
    }
}
