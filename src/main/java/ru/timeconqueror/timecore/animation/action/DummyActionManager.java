package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.action.IDelayedAction;

public class DummyActionManager<T extends AnimatedObject<T>> implements ru.timeconqueror.timecore.api.animation.ActionManager<T> {
    public DummyActionManager(BaseAnimationManager animationManager, T tileEntity, PredefinedAnimations validatedPredefines) {

    }

    @Override
    public <EXTRA_DATA> void enableAction(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData) {

    }

    @Override
    public boolean isActionEnabled(IDelayedAction<T, ?> action) {
        return false;
    }

    @Override
    public <EXTRA_DATA> void disableAction(IDelayedAction<T, EXTRA_DATA> action) {

    }

    @Override
    public <EXTRA_DATA> void updateActionData(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA newData) {

    }

    @Override
    public AnimationManager getAnimationManager() {
        return null;
    }
}
