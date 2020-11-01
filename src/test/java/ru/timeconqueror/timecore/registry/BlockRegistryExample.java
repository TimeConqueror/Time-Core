package ru.timeconqueror.timecore.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.block.DummyBlockWithTileEntity;
import ru.timeconqueror.timecore.registry.newreg.BlockRegister;

import static ru.timeconqueror.timecore.util.Hacks.promise;

@ObjectHolder(TimeCore.MODID)
public class BlockRegistryExample {
    public static final Block TEST_SAND = promise();
    public static final Block TEST_DIRT = promise();
    public static final Block TEST_EMERALD_ORE = promise();
    public static final Block TEST_BLOCK_WITH_TILE = promise();

    private static class Init {
        @TimeAutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @TimeAutoRegistrable.InitMethod
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            REGISTER.register("test_sand", () -> new Block(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .apply(chain -> {
                        BlockModelLocation modelLocation = new BlockModelLocation(chain.getModId(), "block/test_sand");
                        chain
                                .genModel(modelLocation, () -> BlockModel.createCubeAllModel(new TextureLocation("minecraft", "block/sand")))
                                .genDefaultState(modelLocation);
                    });

            REGISTER.register("test_dirt", () -> new Block(propsCreator.create()))
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"));

            REGISTER.register("test_emerald_ore", () -> new Block(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/emerald_ore"));

            REGISTER.register("test_block_with_tile", () -> new DummyBlockWithTileEntity(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/furnace"));
        }
    }
}
