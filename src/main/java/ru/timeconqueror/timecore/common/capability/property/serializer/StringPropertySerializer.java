package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class StringPropertySerializer implements IPropertySerializer<String> {
    public static final IPropertySerializer<String> INSTANCE = new StringPropertySerializer();
    public static final IPropertySerializer<String> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, String value, CompoundTag nbt) {
        nbt.putString(name, value);
    }

    @Override
    public String deserialize(String name, CompoundTag nbt) {
        return nbt.getString(name);
    }
}
