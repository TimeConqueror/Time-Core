package ru.timeconqueror.timecore.registry_example.deferred;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;
import ru.timeconqueror.timecore.block.TestCobbleBlock;

public class BlockDeferredRegistryExample {
    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);
    private static final BlockPropsFactory BLOCK_PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

    public static RegistryObject<TestCobbleBlock> TEST_COBBLE = REGISTER.register("test_cobble", () -> new TestCobbleBlock(BLOCK_PROPS_CREATOR.create()))
            .oneVarStateAndCubeAllModel(new TextureLocation("minecraft", "block/cobblestone"))
            .defaultBlockItem(ItemGroup.TAB_MISC)
//          .genLangEntry("Test Cobble")
            .asRegistryObject();

    public static RegistryObject<Block> TEST_GRAVEL = REGISTER.register("test_gravel", () -> new Block(BLOCK_PROPS_CREATOR.create()))
            .defaultBlockItem(ItemGroup.TAB_MISC)
            .asRegistryObject();
}
