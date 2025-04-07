package ru.timeconqueror.timecore.api.util;

import net.minecraft.core.BlockPos;

import java.util.Iterator;

public class BlockPosUtils {

    /**
     * Creates iterator for provided area.
     * Positions with {@code xSize}, {@code ySize} or {@code zSize} coords are excluded.
     */
    public static Iterable<BlockPos> between(BlockPos startPos, int xSize, int ySize, int zSize) {
        return () -> new Iterator<>() {

            long xyz;
            final BlockPos.MutableBlockPos pos = startPos.mutable();
            final long xyzSize = (long) xSize * ySize * zSize;

            @Override
            public boolean hasNext() {
                return xyz < xyzSize;
            }

            @Override
            public BlockPos next() {
                long yz = xyz / xSize;
                int x = (int) (xyz % xSize);
                int y = (int) (yz % ySize);
                int z = (int) (yz / ySize);
                xyz++;

                return pos.setWithOffset(startPos, x, y, z);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("You can not modify block iterator");
            }
        };
    }

    public static BlockPos makeMin(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
    }

    public static BlockPos makeMax(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );
    }
}