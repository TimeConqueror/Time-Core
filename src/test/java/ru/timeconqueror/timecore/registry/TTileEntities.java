package ru.timeconqueror.timecore.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.timecore.api.registry.TileEntityTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.block.DummyTileEntity;
import ru.timeconqueror.timecore.client.DummyTileEntityRenderer;

@TimeAutoRegistrable
public class TTileEntities extends TileEntityTimeRegistry {
    public static TileEntityType<DummyTileEntity> TEST_TE_TYPE;

    @Override
    public void register() {
        TEST_TE_TYPE = regTileEntity(DummyTileEntity.class, DummyTileEntity::new, "test_tile", TBlocks.TEST_BLOCK_WITH_TILE)
                .regCustomRenderer(() -> DummyTileEntityRenderer::new)
                .retrieveTileEntityType();
    }
}
