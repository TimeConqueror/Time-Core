package ru.timeconqueror.timecore.registry;

import net.minecraft.util.ResourceLocation;

public class InsertablePromised<T> extends Promised<T> {
    public InsertablePromised(ResourceLocation id) {
        super(id);
    }

    public void insert(T value) {
        this.value = value;
    }
}
