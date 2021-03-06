package ru.timeconqueror.timecore.registry_example.deferred;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;
import ru.timeconqueror.timecore.client.DummyTileEntityRenderer;
import ru.timeconqueror.timecore.registry_example.BlockRegistryExample;

public class TileEntityDeferredRegistryExample {
    @AutoRegistrable
    private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

    public static RegistryObject<TileEntityType<DummyTileEntity>> TEST_TE_TYPE = REGISTER.registerSingleBound("test_tile", DummyTileEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE)
            .regCustomRenderer(() -> DummyTileEntityRenderer::new)
            .asRegistryObject();
}
