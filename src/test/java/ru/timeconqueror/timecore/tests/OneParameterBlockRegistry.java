package ru.timeconqueror.timecore.tests;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class OneParameterBlockRegistry {
    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

    @AutoRegistrable.InitMethod
    private static void register(FMLConstructModEvent event) {
        REGISTER.register("one_parameter_test", () -> new Block(AbstractBlock.Properties.of(Material.AIR))).defaultBlockItem(ItemGroup.TAB_MISC);
    }
}
