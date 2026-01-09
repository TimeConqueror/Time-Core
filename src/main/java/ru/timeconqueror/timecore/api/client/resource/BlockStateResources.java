package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.client.resource.KtBlockStateResourceFactory;
import ru.timeconqueror.timecore.client.resource.StairsBlockStateResourceFactory;

public class BlockStateResources {
    public static BlockStateResource stairs(BlockModelLocation stairs, BlockModelLocation innerStairs, BlockModelLocation outerStairs) {
        return StairsBlockStateResourceFactory.create(stairs, innerStairs, outerStairs);
    }

    public static BlockStateResource slab(BlockModelLocation modelBottom, BlockModelLocation modelDouble, BlockModelLocation modelTop) {
        return KtBlockStateResourceFactory.slab(modelBottom, modelDouble, modelTop);
    }

    public static BlockStateResource singleVariantWithSingleModel(BlockModelLocation model) {
        return KtBlockStateResourceFactory.singleVariantWithSingleModel(model);
    }
}
