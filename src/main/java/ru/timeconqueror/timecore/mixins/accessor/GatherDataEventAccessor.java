package ru.timeconqueror.timecore.mixins.accessor;

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GatherDataEvent.class, remap = false)
public interface GatherDataEventAccessor {
    @Accessor
    GatherDataEvent.DataGeneratorConfig getConfig();
}