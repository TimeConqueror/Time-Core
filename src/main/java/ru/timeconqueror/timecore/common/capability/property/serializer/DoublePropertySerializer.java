package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class DoublePropertySerializer implements IPropertySerializer<Double>{
    public static final IPropertySerializer<Double> INSTANCE = new DoublePropertySerializer();
    public static final IPropertySerializer<Double> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, Double value, CompoundTag nbt) {
        nbt.putDouble(name, value);
    }

    @Override
    public Double deserialize(String name, CompoundTag nbt) {
        return nbt.getDouble(name);
    }
}
