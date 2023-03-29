package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.HeatCubeBlock;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@AutoRegistrable.Entries("block")
public class ABlockRegistry {
    public static HeatCubeBlock HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            REGISTER.register("heat_cube", () -> new HeatCubeBlock(propsCreator.create()))
                    .defaultBlockItem(CreativeModeTab.TAB_TOOLS);
        }
    }
}
