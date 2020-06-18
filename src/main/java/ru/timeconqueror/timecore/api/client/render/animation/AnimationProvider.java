package ru.timeconqueror.timecore.api.client.render.animation;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.StateMachine;

public interface AnimationProvider<T extends Entity> {
    @NotNull
    StateMachine<T> getStateMachine();
}
