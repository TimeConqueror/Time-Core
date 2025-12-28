package ru.timeconqueror.timecore.structureio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Getter
@RequiredArgsConstructor
public class ExtendedStructureTemplate {
    public static final ExtendedStructureTemplate DUMMY = new ExtendedStructureTemplate(new StructureTemplate(), BlockPos.ZERO);

    private final StructureTemplate template;
    private final BlockPos genOffset;
}
