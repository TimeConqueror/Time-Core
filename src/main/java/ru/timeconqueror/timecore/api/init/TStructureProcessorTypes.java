package ru.timeconqueror.timecore.api.init;

import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.common.world.structure.processor.RandomizeBlockProcessor;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.StructureProcessorTypeRegister;

public class TStructureProcessorTypes {
    @AutoRegistrable
    private static final StructureProcessorTypeRegister REGISTER = new StructureProcessorTypeRegister(TimeCore.MODID);

    public static final IStructureProcessorType<RandomizeBlockProcessor> RANDOMIZE_BLOCK_PROCESSOR = REGISTER.register("randomize_block", RandomizeBlockProcessor.CODEC);
}
