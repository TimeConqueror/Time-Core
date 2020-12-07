package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.blockstates.StairsBlockStateFactory;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;

public class BlockStateResources {
    public static BlockStateResource stairs(BlockModelLocation stairs, BlockModelLocation innerStairs, BlockModelLocation outerStairs) {
        return StairsBlockStateFactory.create(stairs, innerStairs, outerStairs);
    }
}
