package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;

public class BooleanPropertySerializer implements IPropertySerializer<Boolean> {
    public static final IPropertySerializer<Boolean> INSTANCE = new BooleanPropertySerializer();
    public static final IPropertySerializer<Boolean> NULLABLE = IPropertySerializer.nullable(INSTANCE);

    @Override
    public void serialize(String name, Boolean value, CompoundTag nbt) {
        nbt.putBoolean(name, value);
    }

    @Override
    public Boolean deserialize(String name, CompoundTag nbt) {
        return nbt.getBoolean(name);
    }


}
