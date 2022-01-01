package ru.timeconqueror.timecore.api.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * Tile entity with some useful methods
 */
public abstract class SimpleTile extends BlockEntity {
    public SimpleTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public boolean isServerSide() {
        return !isClientSide();
    }

    public boolean isClientSide() {
        Level level = Objects.requireNonNull(getLevel());
        return level.isClientSide();
    }

    /**
     * Returns the blockstate on tileentity pos.
     */
    public BlockState getState() {
        Level level = Objects.requireNonNull(getLevel());

        return level.getBlockState(worldPosition);
    }
}
