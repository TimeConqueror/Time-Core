package ru.timeconqueror.timecore.registry;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public interface Promised<T> extends Supplier<T> {
    ResourceLocation getId();


}
