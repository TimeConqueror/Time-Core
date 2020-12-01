package ru.timeconqueror.timecore.devtools.gen.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface ISaveFunction {
    Advancement process(Advancement.Builder builder, ResourceLocation relPath);
}
