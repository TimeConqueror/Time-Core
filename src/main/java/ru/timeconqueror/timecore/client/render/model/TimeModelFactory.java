package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

@FunctionalInterface
public interface TimeModelFactory {
    TimeModel create(Function<ResourceLocation, RenderType> renderTypeProvider);
}
