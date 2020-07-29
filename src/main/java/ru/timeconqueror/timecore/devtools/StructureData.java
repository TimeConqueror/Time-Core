package ru.timeconqueror.timecore.devtools;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Objects;

public class StructureData {
    private final AxisAlignedBB bb;
    private final ResourceLocation structureName;
    private final int dimensionId;

    public StructureData(AxisAlignedBB bb, ResourceLocation structureName, int dimensionId) {
        this.bb = bb;
        this.structureName = structureName;
        this.dimensionId = dimensionId;
    }

    public StructureData(AxisAlignedBB bb, Structure<?> structure, DimensionType dimensionType) {
        this.bb = bb;
        this.structureName = structure.getRegistryName();
        this.dimensionId = dimensionType.getId();
    }

    public AxisAlignedBB getBoundingBox() {
        return bb;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StructureData)) return false;
        StructureData that = (StructureData) o;
        return dimensionId == that.dimensionId &&
                bb.equals(that.bb) &&
                structureName.equals(that.structureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bb, structureName, dimensionId);
    }
}
