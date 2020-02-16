package ru.timeconqueror.timecore.client;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import ru.timeconqueror.timecore.block.TestTileEntity;

public class TestTileEntityRenderer extends TileEntityRenderer<TestTileEntity> {
    @Override
    public void render(TestTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
    }
}
