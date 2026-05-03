package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class FloatPropertySerializer implements IPropertySerializer<Float>{
    public static final IPropertySerializer<Float> INSTANCE = new FloatPropertySerializer();
    public static final IPropertySerializer<Float> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, Float value, CompoundTag nbt) {
        nbt.putFloat(name, value);
    }

    @Override
    public Float deserialize(String name, CompoundTag nbt) {
        return nbt.getFloat(name);
    }
}
