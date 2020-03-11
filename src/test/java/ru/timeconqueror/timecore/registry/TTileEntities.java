package ru.timeconqueror.timecore.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.timecore.api.registry.TileEntityTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.block.TestTileEntity;
import ru.timeconqueror.timecore.client.TestTileEntityRenderer;

@TimeAutoRegistrable
public class TTileEntities extends TileEntityTimeRegistry {
    public static TileEntityType<TestTileEntity> TEST_TE_TYPE;

    @Override
    public void register() {
        TEST_TE_TYPE = regTileEntity(TestTileEntity.class, TestTileEntity::new, "test_tile", TBlocks.TEST_BLOCK_WITH_TILE)
                .regCustomRenderer(() -> TestTileEntityRenderer::new)
                .retrieveTileEntityType();
    }
}
