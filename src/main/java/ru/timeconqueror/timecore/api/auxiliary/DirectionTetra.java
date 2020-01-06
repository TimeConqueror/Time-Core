package ru.timeconqueror.timecore.api.auxiliary;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public enum DirectionTetra implements IStringSerializable {
    NORTH(0, 2, "north", 0, -1),
    EAST(1, 3, "east", 1, 0),
    SOUTH(2, 0, "south", 0, 1),
    WEST(3, 1, "west", -1, 0);

    private static final DirectionTetra[] LOOKUP = new DirectionTetra[DirectionTetra.values().length];

    static {
        for (DirectionTetra value : values()) {
            LOOKUP[value.index] = value;
        }
    }

    private final String name;
    private final int index;
    private final int indexOfOpposite;

    /**
     * Offset from center block
     */
    private final int offsetX;
    /**
     * Offset from center block
     */
    private final int offsetZ;

    DirectionTetra(int index, int indexOfOpposite, String name, int offsetX, int offsetZ) {
        this.indexOfOpposite = indexOfOpposite;
        this.index = index;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }

    public static DirectionTetra byIndex(int index) {
        if (index < 0 || index >= values().length) {
            return null;
        }

        return LOOKUP[index];
    }

    public DirectionTetra getOpposite() {
        return byIndex(indexOfOpposite);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public BlockPos getOffsetBlockPos(BlockPos center) {
        return center.add(getOffsetX(), 0, getOffsetZ());
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DirectionTetra{" +
                "name='" + name + '\'' +
                '}';
    }
}
