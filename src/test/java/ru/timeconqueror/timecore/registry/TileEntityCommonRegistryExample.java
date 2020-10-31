package ru.timeconqueror.timecore.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;
import ru.timeconqueror.timecore.client.DummyTileEntityRenderer;
import ru.timeconqueror.timecore.registry.common.TileEntityTimeRegistry;

@TimeAutoRegistrable
public class TileEntityCommonRegistryExample extends TileEntityTimeRegistry {
    public static TileEntityType<DummyTileEntity> TEST_TE_TYPE_1;

    @Override
    public void register() {
        TEST_TE_TYPE_1 = regTileEntity(DummyTileEntity::new, "test_tile_1", BlockRegistryExample.TEST_BLOCK_WITH_TILE)
                .regCustomRenderer(() -> DummyTileEntityRenderer::new)
                .retrieveTileEntityType();
    }
}
