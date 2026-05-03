package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class LongPropertySerializer implements IPropertySerializer<Long> {
    public static final IPropertySerializer<Long> INSTANCE = new LongPropertySerializer();
    public static final IPropertySerializer<Long> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, Long value, CompoundTag nbt) {
        nbt.putLong(name, value);
    }

    @Override
    public Long deserialize(String name, CompoundTag nbt) {
        return nbt.getLong(name);
    }
}
