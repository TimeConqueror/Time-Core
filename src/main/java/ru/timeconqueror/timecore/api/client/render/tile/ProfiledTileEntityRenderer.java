package ru.timeconqueror.timecore.api.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ProfiledTileEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final BlockEntityRenderer<? super T> delegate;

    public ProfiledTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn, Function<? super BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> delegateProvider) {
        super();

        delegate = delegateProvider.apply(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        profiler.push(tileEntityIn.getType().getRegistryName().toString());

        delegate.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

        profiler.pop();
    }

    @Override
    public boolean shouldRenderOffScreen(T te) {
        return delegate.shouldRenderOffScreen(te);
    }
}
