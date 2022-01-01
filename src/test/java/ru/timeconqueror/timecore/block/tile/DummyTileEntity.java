package ru.timeconqueror.timecore.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.timecore.registry_example.deferred.TileEntityDeferredRegistryExample;

public class DummyTileEntity extends BlockEntity {
    public DummyTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityDeferredRegistryExample.TEST_TE_TYPE.get(), pos, state);

        System.out.println("Me placed");

        System.out.println(getType());
    }
}
