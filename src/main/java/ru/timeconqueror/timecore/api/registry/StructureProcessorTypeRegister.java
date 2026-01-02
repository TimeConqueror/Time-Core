package ru.timeconqueror.timecore.api.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class StructureProcessorTypeRegister extends SimpleVanillaRegister<StructureProcessorType<?>> {
    public StructureProcessorTypeRegister(String modId) {
        super(Registries.STRUCTURE_PROCESSOR, modId);
    }

    public <P extends StructureProcessor> StructureProcessorType<P> register(String name, MapCodec<P> codec) {
        StructureProcessorType<P> type = () -> codec;
        registerEntry(name, () -> type);

        return type;
    }
}
