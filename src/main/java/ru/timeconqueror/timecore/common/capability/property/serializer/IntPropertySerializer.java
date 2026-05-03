package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class IntPropertySerializer implements IPropertySerializer<Integer>{
    public static final IPropertySerializer<Integer> INSTANCE = new IntPropertySerializer();
    public static final IPropertySerializer<Integer> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, Integer value, CompoundTag nbt) {
        nbt.putInt(name, value);
    }

    @Override
    public Integer deserialize(String name, CompoundTag nbt) {
        return nbt.getInt(name);
    }
}
