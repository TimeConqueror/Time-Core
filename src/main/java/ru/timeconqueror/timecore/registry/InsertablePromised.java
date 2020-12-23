package ru.timeconqueror.timecore.registry;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class InsertablePromised<T> implements Promised<T> {
    private final ResourceLocation id;
    @Nullable
    private T value = null;

    public InsertablePromised(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public T get() {
        return null;
    }

    public void insert(T value) {
        this.value = value;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
