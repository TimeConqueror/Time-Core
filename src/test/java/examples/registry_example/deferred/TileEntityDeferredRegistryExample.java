package examples.registry_example.deferred;

import examples.block.tile.DummyTileEntity;
import examples.client.DummyTileEntityRenderer;
import examples.registry_example.BlockRegistryExample;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

public class TileEntityDeferredRegistryExample {
    @AutoRegistrable
    private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

    public static Promised<BlockEntityType<DummyTileEntity>> TEST_TE_TYPE = REGISTER.registerSingleBound("test_tile", DummyTileEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE)
            .regCustomRenderer(() -> DummyTileEntityRenderer::new)
            .asPromised();
}
