package ru.timeconqueror.timecore.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.block.DummyBlockWithTileEntity;
import ru.timeconqueror.timecore.registry.common.BlockTimeRegistry;

@TimeAutoRegistrable
public class BlockCommonRegistryExample extends BlockTimeRegistry {
    private static final BlockPropsFactory PROPS_CREATOR = new BlockPropsFactory(() -> Block.Properties.create(Material.ROCK));
    public static final Block MC_SAND = new Block(PROPS_CREATOR.create());
    public static final Block MC_DIRT = new Block(PROPS_CREATOR.create());
    public static final Block MC_EMERALD_ORE = new Block(PROPS_CREATOR.create());

    public static final Block TEST_BLOCK_WITH_TILE = new DummyBlockWithTileEntity(PROPS_CREATOR.create());

    @Override
    public void register() {
        BlockModelLocation modelLocation = new BlockModelLocation(getModID(), "block/test_sand");
        regBlock(MC_SAND, "test_sand")
                .regItemBlock(ItemGroup.MISC)
                .genModel(modelLocation, () -> BlockModel.createCubeAllModel(new TextureLocation("minecraft", "block/sand")))
                .genDefaultState(modelLocation);
        regBlock(MC_DIRT, "test_dirt")
                .regItemBlock(ItemGroup.MISC)
                .genDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"));

        regBlock(MC_EMERALD_ORE, "test_emerald_ore")
                .genDefaultStateAndModel(new TextureLocation("minecraft", "block/emerald_ore"))
                .regItemBlock(ItemGroup.MISC);
        regBlock(TEST_BLOCK_WITH_TILE, "test_block_with_tile")
                .genDefaultStateAndModel(new TextureLocation("minecraft", "block/furnace"))
                .regItemBlock(ItemGroup.MISC);
    }
}
