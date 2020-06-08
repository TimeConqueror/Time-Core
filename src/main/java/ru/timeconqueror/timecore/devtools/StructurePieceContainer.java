package ru.timeconqueror.timecore.devtools;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class StructurePieceContainer {
    private final ResourceLocation structureName;
    private final AxisAlignedBB bb;

    public StructurePieceContainer(ResourceLocation structureName, AxisAlignedBB bb) {
        this.structureName = structureName;
        this.bb = bb;
    }

    public AxisAlignedBB getBb() {
        return bb;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }
}
