package ru.timeconqueror.timecore.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.common.registry.block.BlockPropertiesFactory;
import ru.timeconqueror.timecore.api.common.registry.block.BlockTimeRegistry;

@TimeAutoRegistry
public class TBlocks extends BlockTimeRegistry {
    private static BlockPropertiesFactory propsCreator = new BlockPropertiesFactory(() -> Block.Properties.create(Material.ROCK));
    public static Block mcSand = new Block(propsCreator.createProps());
    public static Block mcDirt = new Block(propsCreator.createProps());
    public static Block mcEmeraldOre = new Block(propsCreator.createProps());

    public TBlocks() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {
        BlockModelLocation modelLocation = new BlockModelLocation(getModID(), "block/test_sand");
        regBlock(mcSand, "test_sand")
                .regModel(modelLocation, BlockModel.createCubeAllModel(new TextureLocation("minecraft", "block/sand")))
                .regDefaultState(modelLocation)
                .regItemBlock(ItemGroup.MISC);
        regBlock(mcDirt, "test_dirt")
                .regDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"))
                .regItemBlock(ItemGroup.MISC);

        regBlock(mcEmeraldOre, "test_emerald_ore").regDefaults(new TextureLocation("minecraft", "block/emerald_ore"), ItemGroup.MISC);
    }
}
