package ru.timeconqueror.timecore.common.capability.property;

import net.minecraft.nbt.CompoundTag;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

public class CoffeeProperty<T> {
    private final String name;
    private final IPropertySerializer<T> serializer;
    private final boolean externallyMutable;

    private boolean clientDependent = false;
    private boolean shouldBeSynced = false;
    private boolean changed = false;

    private T value;

    public CoffeeProperty(String name, T value, IPropertySerializer<T> serializer) {
        this.name = name;
        this.value = value;
        this.serializer = serializer;
        this.externallyMutable = value instanceof MutableProperty;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.changed = true;
        this.value = value;
    }


    public CoffeeProperty<T> clientDependent() {
        clientDependent = true;
        return this;
    }

    public CoffeeProperty<T> synced() {
        shouldBeSynced = true;
        return this;
    }

    public boolean isClientDependent() {
        return clientDependent;
    }

    public boolean isChanged() {
        if (externallyMutable) {
            return ((MutableProperty) value).isChanged() || changed;
        }
        return changed;
    }

    public void setChanged(boolean changed) {
        if (externallyMutable) {
            ((MutableProperty) value).setChanged(changed);
        }
        this.changed = changed;
    }

    public void serialize(CompoundTag nbt, SerializationType type) {
        if (type == SerializationType.SAVE || shouldBeSynced) {
            serializer.serialize(name, value, nbt);
        }
    }

    public void deserialize(CompoundTag nbt) {
        if (nbt.contains(name)) {
            this.value = serializer.deserialize(name, nbt);
            this.changed = false;
        }
    }

    public void deserialize(CompoundTag nbt, boolean fromClient) {
        if (fromClient == clientDependent) {
            deserialize(nbt);
        }
    }

    // ==============================
    //         Kotlin compat
    // ==============================

    public T getValue(Object thisRef, kotlin.reflect.KProperty<?> property) {
        return get();
    }

    public void setValue(Object thisRef, kotlin.reflect.KProperty<?> property, T value) {
        set(value);
    }
}
