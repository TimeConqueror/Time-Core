package ru.timeconqueror.timecore.client;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import ru.timeconqueror.timecore.block.DummyTileEntity;

public class DummyTileEntityRenderer extends TileEntityRenderer<DummyTileEntity> {
    @Override
    public void render(DummyTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
    }
}
