package ru.timeconqueror.timecore.api.common.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class TimeSound extends SoundEvent {
    public TimeSound(ResourceLocation name) {
        super(name);
    }

    public ResourceLocation location() {
        return location;
    }
}
