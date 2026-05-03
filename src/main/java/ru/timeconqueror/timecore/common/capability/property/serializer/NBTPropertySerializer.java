package ru.timeconqueror.timecore.common.capability.property.serializer;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import ru.timeconqueror.timecore.api.util.INBTSimpleSerializable;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class NBTPropertySerializer<T extends INBTSimpleSerializable<CompoundTag>> implements IPropertySerializer<T>{
    private final Supplier<T> factory;

    @Override
    public void serialize(String name, T value, CompoundTag nbt) {
        nbt.put(name, value.serializeNBT());
    }

    @Override
    public T deserialize(String name, CompoundTag nbt) {
        T value = factory.get();
        value.deserializeNBT(nbt.getCompound(name));
        return value;
    }
}
