package ru.timeconqueror.timecore.common.capability.property.serializer;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;

@RequiredArgsConstructor
public class NullPropertySerializer<T> implements IPropertySerializer<T> {
    private final IPropertySerializer<T> prop;

    @Override
    public void serialize(String name, T value, CompoundTag nbt) {
        CompoundTag comp = new CompoundTag();
        if (value != null) {
            prop.serialize("value", value, comp);
        }
        nbt.put(name, comp);
    }

    @Override
    public T deserialize(String name, CompoundTag nbt) {
        CompoundTag comp = nbt.getCompound(name);
        if (comp.contains("value")) {
            return prop.deserialize("value", comp);
        }
        return null;
    }
}
