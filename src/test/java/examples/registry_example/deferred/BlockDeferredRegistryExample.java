package examples.registry_example.deferred;

import examples.block.TestCobbleBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;
import ru.timeconqueror.timecore.api.registry.util.Promised;

public class BlockDeferredRegistryExample {
    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);
    private static final BlockPropsFactory BLOCK_PROPS_CREATOR = new BlockPropsFactory(Block.Properties::of);

    public static Promised<TestCobbleBlock> TEST_COBBLE = REGISTER.register("test_cobble", () -> new TestCobbleBlock(BLOCK_PROPS_CREATOR.create()))
            .oneVarStateAndCubeAllModel(new TextureLocation("minecraft", "block/cobblestone"))
            .defaultBlockItem(CreativeModeTabs.TOOLS_AND_UTILITIES)
//          .genLangEntry("Test Cobble")
            .asPromised();

    public static Promised<Block> TEST_GRAVEL = REGISTER.register("test_gravel", () -> new Block(BLOCK_PROPS_CREATOR.create()))
            .defaultBlockItem(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .asPromised();
}
