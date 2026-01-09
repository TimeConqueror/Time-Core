package examples.registry_example;

import examples.block.blockentity.DummyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.BlockEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.RegistryKeys;
import ru.timeconqueror.timecore.api.util.Hacks;

@AutoRegistrable.Entries(RegistryKeys.BLOCK_ENTITY_TYPES)
public class BlockEntityRegistryExample {
    public static BlockEntityType<DummyBlockEntity> TEST_TILE_1 = Hacks.promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockEntityRegister REGISTER = new BlockEntityRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            REGISTER.registerSingleBound("test_tile_1", DummyBlockEntity::new, () -> BlockRegistryExample.TEST_BLOCK_WITH_TILE);
        }
    }
}
