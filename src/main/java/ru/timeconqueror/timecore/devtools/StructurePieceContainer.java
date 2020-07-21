package ru.timeconqueror.timecore.devtools;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Objects;

public class StructurePieceContainer {
    private final ResourceLocation structureName;
    private final AxisAlignedBB bb;
    private final int dimension;

    public StructurePieceContainer(ResourceLocation structureName, AxisAlignedBB bb, int dimension) {
        this.structureName = structureName;
        this.bb = bb;
        this.dimension = dimension;
    }

    public AxisAlignedBB getBb() {
        return bb;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StructurePieceContainer)) return false;
        StructurePieceContainer that = (StructurePieceContainer) o;
        return dimension == that.dimension &&
                structureName.equals(that.structureName) &&
                bb.equals(that.bb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structureName, bb, dimension);
    }
}
