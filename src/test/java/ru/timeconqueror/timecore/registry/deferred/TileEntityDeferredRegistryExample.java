package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;
import ru.timeconqueror.timecore.client.DummyTileEntityRenderer;
import ru.timeconqueror.timecore.registry.BlockCommonRegistryExample;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

public class TileEntityDeferredRegistryExample {
    @TimeAutoRegistrable
    private static final DeferredTileEntityRegister REGISTER = new DeferredTileEntityRegister(TimeCore.MODID);

    public static RegistryObject<TileEntityType<DummyTileEntity>> TEST_TE_TYPE = REGISTER.regTileEntityType("test_tile", DummyTileEntity::new, () -> new Block[]{BlockCommonRegistryExample.TEST_BLOCK_WITH_TILE})
            .regCustomRenderer(DummyTileEntityRenderer::new)
            .endTyped();
}
