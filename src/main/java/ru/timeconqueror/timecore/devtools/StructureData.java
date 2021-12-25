package ru.timeconqueror.timecore.devtools;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

public class StructureData {
    private final AABB bb;
    private final ResourceLocation structureName;
    private final ResourceLocation worldId;

    public StructureData(AABB bb, ResourceLocation structureName, ResourceLocation worldId) {
        this.bb = bb;
        this.structureName = structureName;
        this.worldId = worldId;
    }

    public StructureData(AABB bb, StructureFeature<?> structure, Level world) {
        this.bb = bb;
        this.structureName = structure.getRegistryName();
        this.worldId = world.dimension().location();
    }

    public AABB getBoundingBox() {
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
        return bb.equals(that.bb) &&
                structureName.equals(that.structureName) &&
                worldId.equals(that.worldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bb, structureName, worldId);
    }
}
