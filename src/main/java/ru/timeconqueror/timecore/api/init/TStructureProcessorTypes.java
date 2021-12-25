package ru.timeconqueror.timecore.api.init;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.world.structure.processor.RandomizeBlockProcessor;
import ru.timeconqueror.timecore.api.registry.StructureProcessorTypeRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class TStructureProcessorTypes {
    @AutoRegistrable
    private static final StructureProcessorTypeRegister REGISTER = new StructureProcessorTypeRegister(TimeCore.MODID);

    public static final StructureProcessorType<RandomizeBlockProcessor> RANDOMIZE_BLOCK_PROCESSOR = REGISTER.register("randomize_block", RandomizeBlockProcessor.CODEC);
}
