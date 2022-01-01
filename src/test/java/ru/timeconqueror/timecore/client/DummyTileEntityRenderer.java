package ru.timeconqueror.timecore.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;

@OnlyIn(Dist.CLIENT)
public class DummyTileEntityRenderer extends BlockEntityRenderer<DummyTileEntity> {

    public DummyTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {}

    @Override
    public void render(DummyTileEntity tileEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
}
