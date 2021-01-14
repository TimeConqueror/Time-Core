package ru.timeconqueror.timecore.api.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Tile entity with some useful methods
 */
public abstract class SimpleTile extends TileEntity {
    public SimpleTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public boolean isServerSide() {
        return !isClientSide();
    }

    public boolean isClientSide() {
        World level = Objects.requireNonNull(getLevel());
        return level.isClientSide();
    }

    /**
     * Returns the blockstate on tileentity pos.
     */
    public BlockState getState() {
        World level = Objects.requireNonNull(getLevel());

        return level.getBlockState(worldPosition);
    }
}
