package ru.timeconqueror.timecore.api.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ProfiledTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
    private final TileEntityRenderer<? super T> delegate;

    public ProfiledTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, Function<? super TileEntityRendererDispatcher, TileEntityRenderer<? super T>> delegateProvider) {
        super(rendererDispatcherIn);

        delegate = delegateProvider.apply(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        IProfiler profiler = Minecraft.getInstance().getProfiler();
        profiler.push(tileEntityIn.getType().getRegistryName().toString());

        delegate.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

        profiler.pop();
    }

    @Override
    public boolean shouldRenderOffScreen(T te) {
        return delegate.shouldRenderOffScreen(te);
    }
}
