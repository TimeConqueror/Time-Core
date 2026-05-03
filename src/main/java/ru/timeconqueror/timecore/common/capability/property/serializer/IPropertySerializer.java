package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public interface IPropertySerializer<T> {
    void serialize(String name, T value, CompoundTag nbt);
    T deserialize(String name, CompoundTag nbt);

    static <T> IPropertySerializer<T> nullable(IPropertySerializer<T> serializer) {
        return new NullPropertySerializer<>(serializer);
    }
}
