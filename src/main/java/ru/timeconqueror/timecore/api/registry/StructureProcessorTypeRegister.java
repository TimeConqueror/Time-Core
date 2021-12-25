package ru.timeconqueror.timecore.api.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class StructureProcessorTypeRegister extends VanillaRegister<StructureProcessorType<?>> {
    public StructureProcessorTypeRegister(String modId) {
        super(modId, Registry.STRUCTURE_PROCESSOR);
    }

    public <P extends StructureProcessor> StructureProcessorType<P> register(String name, Codec<P> codec) {
        StructureProcessorType<P> type = () -> codec;
        registerEntry(name, () -> type);

        return type;
    }
}
