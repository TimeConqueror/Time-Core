package ru.timeconqueror.timecore.animation_example.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;

public class RenderTESR extends TileEntityRenderer<DummyTileEntity> {
    public RenderTESR(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(DummyTileEntity dummyTileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }
}
