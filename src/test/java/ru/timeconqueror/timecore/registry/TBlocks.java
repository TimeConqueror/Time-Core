package ru.timeconqueror.timecore.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.registry.block.BlockPropsFactory;
import ru.timeconqueror.timecore.api.registry.block.BlockTimeRegistry;

@TimeAutoRegistry
public class TBlocks extends BlockTimeRegistry {
    private static BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.create(Material.ROCK));
    public static Block MC_SAND = new Block(propsCreator.create());
    public static Block MC_DIRT = new Block(propsCreator.create());
    public static Block MC_EMERALD_ORE = new Block(propsCreator.create());

    public static Block TEST_BLOCK_WITH_TILE = new Block(propsCreator.create());

    @Override
    public void register() {
        BlockModelLocation modelLocation = new BlockModelLocation(getModID(), "block/test_sand");
        regBlock(MC_SAND, "test_sand")
                .regItemBlock(ItemGroup.MISC)
                .regModel(modelLocation, () -> BlockModel.createCubeAllModel(new TextureLocation("minecraft", "block/sand")))
                .regDefaultState(modelLocation);
        regBlock(MC_DIRT, "test_dirt")
                .regItemBlock(ItemGroup.MISC)
                .regDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"));

        regBlock(MC_EMERALD_ORE, "test_emerald_ore")
                .regDefaults(new TextureLocation("minecraft", "block/emerald_ore"), ItemGroup.MISC);
        regBlock(TEST_BLOCK_WITH_TILE, "test_block_with_tile").regDefaults(new TextureLocation("minecraft", "block/furnace"), ItemGroup.MISC);
    }
}
