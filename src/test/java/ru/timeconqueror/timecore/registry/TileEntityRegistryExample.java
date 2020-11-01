package ru.timeconqueror.timecore.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;
import ru.timeconqueror.timecore.registry.newreg.TileEntityRegister;
import ru.timeconqueror.timecore.util.Hacks;

@ObjectHolder(TimeCore.MODID)
public class TileEntityRegistryExample {
    public static final TileEntityType<DummyTileEntity> TEST_TILE_1 = Hacks.promise();

    private static class Init {
        @TimeAutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

        @TimeAutoRegistrable.InitMethod
        private static void register() {
            REGISTER.registerSingleBound("test_tile_1", DummyTileEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE);
        }
    }
}
