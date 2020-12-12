package ru.timeconqueror.timecore.registry.newreg;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class StructureProcessorTypeRegister extends VanillaRegister<IStructureProcessorType<?>> {
    public StructureProcessorTypeRegister(String modId) {
        super(modId, Registry.STRUCTURE_PROCESSOR);
    }

    public <P extends StructureProcessor> IStructureProcessorType<P> register(String name, Codec<P> codec) {
        IStructureProcessorType<P> type = () -> codec;
        addEntry(name, type);

        return type;
    }
}
