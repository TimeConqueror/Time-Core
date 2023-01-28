package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.HeatCubeBlock;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import java.util.Random;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@AutoRegistrable.Entries(value = TimeCore.MODID, registryKey = "block")
public class ABlockRegistry {
    public static HeatCubeBlock HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            REGISTER.register("heat_cube", () -> new HeatCubeBlock(propsCreator.create()))
                    .defaultBlockItem(CreativeModeTabs.TOOLS_AND_UTILITIES);
        }
    }
}
