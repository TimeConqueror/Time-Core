package ru.timeconqueror.timecore.tests;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class OneParameterBlockRegistry {
    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

    @AutoRegistrable.Init
    private static void register(FMLConstructModEvent event) {
        REGISTER.register("one_parameter_test", () -> new Block(BlockBehaviour.Properties.of(Material.AIR))).defaultBlockItem(CreativeModeTab.TAB_TOOLS).name("One Parameter Test");
    }
}
