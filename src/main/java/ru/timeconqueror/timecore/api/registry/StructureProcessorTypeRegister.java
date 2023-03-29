package ru.timeconqueror.timecore.api.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class StructureProcessorTypeRegister extends SimpleVanillaRegister<StructureProcessorType<?>> {
    public StructureProcessorTypeRegister(String modId) {
        super(Registry.STRUCTURE_PROCESSOR, modId);
    }

    public <P extends StructureProcessor> StructureProcessorType<P> register(String name, Codec<P> codec) {
        StructureProcessorType<P> type = () -> codec;
        registerEntry(name, () -> type);

        return type;
    }
}
