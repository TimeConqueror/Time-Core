package examples.animation_example.block_example.registry;

import examples.animation_example.block_example.block.HeatCubeBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;
import ru.timeconqueror.timecore.api.registry.util.RegistryKeys;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@AutoRegistrable.Entries(RegistryKeys.BLOCKS)
public class ABlockRegistry {
    public static HeatCubeBlock HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(Block.Properties::of);

            REGISTER.register("heat_cube", () -> new HeatCubeBlock(propsCreator.create()))
                    .blockItemDefaultModel(CreativeModeTabs.TOOLS_AND_UTILITIES);
        }
    }
}
