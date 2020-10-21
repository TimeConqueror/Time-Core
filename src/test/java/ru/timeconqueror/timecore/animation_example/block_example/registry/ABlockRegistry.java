package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.HeatCubeBlock;
import ru.timeconqueror.timecore.registry.BlockPropsFactory;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.DeferredBlockRegister;

public class ABlockRegistry {
    @TimeAutoRegistrable
    private static final DeferredBlockRegister REGISTER = new DeferredBlockRegister(TimeCore.MODID);

    private static final BlockPropsFactory PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE).noOcclusion());

    public static RegistryObject<HeatCubeBlock> HEAT_CUBE = REGISTER.regBlock("heat_cube", () -> new HeatCubeBlock(PROPS_CREATOR.create()))
            .regDefaultBlockItem(ItemGroup.TAB_MISC)
            .endTyped();
}
