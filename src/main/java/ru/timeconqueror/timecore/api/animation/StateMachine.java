package ru.timeconqueror.timecore.api.animation;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.animation.DelayedAction;

public interface StateMachine<T extends Entity> {
    void enableAction(DelayedAction<T> action, boolean looped);

    /**
     * Removes action from watched ones and stops an animation on the animation layer, represented by {@link DelayedAction#getAnimationLayer()}
     */
    void disableAction(DelayedAction<T> action);

    AnimationManager getAnimationManager();

    void onTick();
}
