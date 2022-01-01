package ru.timeconqueror.timecore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.timecore.registry_example.deferred.TileEntityDeferredRegistryExample;

import javax.annotation.Nullable;

public class DummyBlockWithTileEntity extends BaseEntityBlock {
    public DummyBlockWithTileEntity(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos_, BlockState state_) {
        return TileEntityDeferredRegistryExample.TEST_TE_TYPE.get().create(pos_, state_);
    }
}
