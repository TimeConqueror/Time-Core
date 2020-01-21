package ru.timeconqueror.timecore.test.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.ModelBlock;
import ru.timeconqueror.timecore.api.client.resource.location.ModelBlockLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.common.registry.block.BlockPropertiesFactory;
import ru.timeconqueror.timecore.common.registry.block.BlockTimeRegistry;

@TimeAutoRegistry
public class TBlocks extends BlockTimeRegistry {
    private static BlockPropertiesFactory propsCreator = new BlockPropertiesFactory(() -> Block.Properties.create(Material.ROCK));
    public static Block mcSand = new Block(propsCreator.createProps());
    public static Block mcDirt = new Block(propsCreator.createProps());

    public TBlocks() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {
        ModelBlockLocation modelLocation = new ModelBlockLocation(getModID(), "block/test_sand");
        regBlock(mcSand, "test_sand")
                .regModel(modelLocation, ModelBlock.createCubeAllModel(new TextureLocation("minecraft", "block/sand")))
                .regDefaultBlockState(modelLocation);
        regBlock(mcDirt, "test_dirt")
                .regDefaultBlockStateAndModel(new TextureLocation("minecraft", "block/dirt"));
    }
}
