package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.HeatCubeBlock;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@ObjectHolder(TimeCore.MODID)
public class ABlockRegistry {
    public static final HeatCubeBlock HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            REGISTER.register("heat_cube", () -> new HeatCubeBlock(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC);
        }
    }
}
