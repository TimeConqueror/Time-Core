package ru.timeconqueror.timecore.api.animation;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.animation.Action;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationManager;

public interface StateMachine<T extends Entity> {
    void enableAction(Action<T> action, boolean looped);

    void disableAction(Action<T> action);

    AnimationManager getAnimationManager();
}
