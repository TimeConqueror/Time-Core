package ru.timeconqueror.timecore.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.registry.TileEntityTimeRegistry;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.block.TestTileEntity;
import ru.timeconqueror.timecore.client.TestTileEntityRenderer;

@TimeAutoRegistry
public class TTileEntities extends TileEntityTimeRegistry {
    public static TileEntityType<TestTileEntity> TEST_TE_TYPE;

    public TTileEntities() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {
        TEST_TE_TYPE = regTileEntity(TestTileEntity::new, "test_tile", TBlocks.TEST_BLOCK_WITH_TILE)
                .regCustomRenderer(() -> TestTileEntityRenderer::new)
                .getTileEntityType();
    }
}
