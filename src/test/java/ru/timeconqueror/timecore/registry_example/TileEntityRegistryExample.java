package ru.timeconqueror.timecore.registry_example;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;

@ObjectHolder(TimeCore.MODID)
public class TileEntityRegistryExample {
    public static final BlockEntityType<DummyTileEntity> TEST_TILE_1 = Hacks.promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.registerSingleBound("test_tile_1", DummyTileEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE);
        }
    }
}
