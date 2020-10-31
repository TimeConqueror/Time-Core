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

@ObjectHolder(TimeCore.MODID)
public class BlockRegistryExample {
    public static final Block TEST_SAND = null;
    public static final Block TEST_DIRT = null;
    public static final Block TEST_EMERALD_ORE = null;
    public static final Block TEST_BLOCK_WITH_TILE = null;

    private static class Init {
        @TimeAutoRegistrable
        private static final BlockRegister BLOCK_REGISTER = new BlockRegister(TimeCore.MODID);

        @TimeAutoRegistrable.InitMethod
        private static void register() {
            BlockPropsFactory PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            BLOCK_REGISTER.register("test_sand", () -> new Block(PROPS_CREATOR.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .apply(chain -> {
                        BlockModelLocation modelLocation = new BlockModelLocation(chain.getModId(), "block/test_sand");
                        chain
                                .genModel(modelLocation, () -> BlockModel.createCubeAllModel(new TextureLocation("minecraft", "blocks/sand")))
                                .genDefaultState(modelLocation);
                    });

            BLOCK_REGISTER.register("test_dirt", () -> new Block(PROPS_CREATOR.create()))
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"));

            BLOCK_REGISTER.register("test_emerald_ore", () -> new Block(PROPS_CREATOR.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/emerald_ore"));

            BLOCK_REGISTER.register("test_block_with_tile", () -> new DummyBlockWithTileEntity(PROPS_CREATOR.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/furnace"));
        }
    }
}
