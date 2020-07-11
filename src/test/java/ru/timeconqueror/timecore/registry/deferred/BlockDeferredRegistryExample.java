package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.block.TestCobbleBlock;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.block.BlockPropsFactory;

public class BlockDeferredRegistryExample {
    @TimeAutoRegistrable
    private static final DeferredBlockRegister REGISTER = new DeferredBlockRegister(TimeCore.MODID);
    private static final BlockPropsFactory BLOCK_PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.create(Material.ROCK));

    public static RegistryObject<TestCobbleBlock> TEST_COBBLE = REGISTER.regBlock("test_cobble", () -> new TestCobbleBlock(BLOCK_PROPS_CREATOR.create()))
            .genDefaultStateAndModel(new TextureLocation("minecraft", "block/cobblestone"))
            .regDefaultBlockItem(ItemGroup.MISC)
            .endTyped();
}
