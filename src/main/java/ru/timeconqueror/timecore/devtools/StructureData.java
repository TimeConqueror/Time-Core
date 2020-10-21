package ru.timeconqueror.timecore.devtools;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Objects;

public class StructureData {
    private final AxisAlignedBB bb;
    private final ResourceLocation structureName;
    private final ResourceLocation worldId;

    public StructureData(AxisAlignedBB bb, ResourceLocation structureName, ResourceLocation worldId) {
        this.bb = bb;
        this.structureName = structureName;
        this.worldId = worldId;
    }

    public StructureData(AxisAlignedBB bb, Structure<?> structure, World world) {
        this.bb = bb;
        this.structureName = structure.getRegistryName();
        this.worldId = world.dimension().location();
    }

    public AxisAlignedBB getBoundingBox() {
        return bb;
    }

    public ResourceLocation getWorldId() {
        return worldId;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StructureData)) return false;
        StructureData that = (StructureData) o;
        return worldId == that.worldId &&
                bb.equals(that.bb) &&
                structureName.equals(that.structureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bb, structureName, worldId);
    }
}
