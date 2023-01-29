package ru.timeconqueror.timecore.registry_example;

import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;
import ru.timeconqueror.timecore.block.tile.DummyTileEntity;

@AutoRegistrable.Entries("block_entity_type")
public class TileEntityRegistryExample {
    public static BlockEntityType<DummyTileEntity> TEST_TILE_1 = Hacks.promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            REGISTER.registerSingleBound("test_tile_1", DummyTileEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE);
        }
    }
}
