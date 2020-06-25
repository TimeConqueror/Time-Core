package ru.timeconqueror.timecore.api.animation;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface AnimationProvider<T extends Entity> {
    @NotNull
    StateMachine<T> getStateMachine();
}
