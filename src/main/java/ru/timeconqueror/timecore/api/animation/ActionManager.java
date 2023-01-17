package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.api.animation.action.IDelayedAction;

public interface ActionManager<T> {

    <EXTRA_DATA> void enableAction(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData);

    /**
     * Returns true if the action is currently active.
     */
    boolean isActionEnabled(IDelayedAction<T, ?> action);

    /**
     * Removes action from watched ones and stops an animation on the animation layer, represented by {@link IDelayedAction#getLayerName()}
     */
    <EXTRA_DATA> void disableAction(IDelayedAction<T, EXTRA_DATA> action);

    /**
     * Sets new data for provided action, if the action is currently active.
     */
    <EXTRA_DATA> void updateActionData(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA newData);

    AnimationManager getAnimationManager();
}
