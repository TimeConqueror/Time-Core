package ru.timeconqueror.timecore.api.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class TimeSound extends SoundEvent {
    public TimeSound(ResourceLocation name) {
        super(name);
    }

    public ResourceLocation location() {
        return location;
    }
}
