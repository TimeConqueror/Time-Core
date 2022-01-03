package ru.timeconqueror.timecore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;

@OnlyIn(Dist.CLIENT)
public class DummyTileEntityRenderer implements BlockEntityRenderer<DummyTileEntity> {

    public DummyTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(DummyTileEntity tileEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
}
