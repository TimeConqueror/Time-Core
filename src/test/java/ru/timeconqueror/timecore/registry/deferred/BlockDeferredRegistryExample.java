package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.block.TestCobbleBlock;
import ru.timeconqueror.timecore.registry.BlockPropsFactory;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

public class BlockDeferredRegistryExample {
    @TimeAutoRegistrable
    private static final DeferredBlockRegister REGISTER = new DeferredBlockRegister(TimeCore.MODID);
    private static final BlockPropsFactory BLOCK_PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

    public static RegistryObject<TestCobbleBlock> TEST_COBBLE = REGISTER.regBlock("test_cobble", () -> new TestCobbleBlock(BLOCK_PROPS_CREATOR.create()))
            .genDefaultStateAndModel(new TextureLocation("minecraft", "block/cobblestone"))
            .regDefaultBlockItem(ItemGroup.TAB_MISC)
//            .genLangEntry("Test Cobble")
            .endTyped();

    public static RegistryObject<Block> TEST_GRAVEL = REGISTER.regBlock("test_gravel", () -> new Block(BLOCK_PROPS_CREATOR.create()))
            .regDefaultBlockItem(ItemGroup.TAB_MISC)
            .endTyped();
}
