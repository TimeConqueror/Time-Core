package ru.timeconqueror.timecore.api.animation.action;

import ru.timeconqueror.timecore.api.animation.AnimatedObject;

/**
 * @see Actions
 */
public interface AnimationUpdateListener<T extends AnimatedObject<T>, DATA> {
    /**
     * Called on server every tick and one extra time upon animation end until this method returns true.
     * Here you can fully control the behavior of attached animated object.
     */
    int onUpdate(ActionContext<T, DATA> ctx);
}