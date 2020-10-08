package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.component.DelayedAction;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.Animation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ActionManagerImpl<T> implements ActionManager<T> {
    private final Set<ActionWatcher<T, ?>> actionWatchers = new HashSet<>();
    private final BaseAnimationManager animationManager;
    private final T boundObject;

    public ActionManagerImpl(BaseAnimationManager animationManager, T boundObject) {
        this.animationManager = animationManager;
        this.boundObject = boundObject;
    }

    @Override
    public <EXTRA_DATA> void enableAction(DelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData) {
        if (actionWatchers.add(new ActionWatcher<>(action, actionData))) {
            action.getAnimationStarter().startAt(animationManager, action.getAnimationLayer());
        }
    }

    @Override
    public boolean isActionEnabled(DelayedAction<T, ?> action) {
        for (ActionWatcher<T, ?> actionWatcher : actionWatchers) {
            if (actionWatcher.stores(action)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <EXTRA_DATA> void disableAction(DelayedAction<T, EXTRA_DATA> action) {
        if (actionWatchers.removeIf(watcher -> watcher.action.equals(action))) {
            animationManager.removeAnimation(action.getAnimationLayer());
        }
    }

    @Override
    public BaseAnimationManager getAnimationManager() {
        return animationManager;
    }

    public Set<ActionWatcher<T, ?>> getActionWatchers() {
        return actionWatchers;
    }

    public T getBoundObject() {
        return boundObject;
    }

    public static class ActionWatcher<T, EXTRA_DATA> {
        private final DelayedAction<T, EXTRA_DATA> action;
        private final EXTRA_DATA actionData;
        private boolean done;

        public ActionWatcher(DelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData) {
            this.action = action;
            this.actionData = actionData;
        }

        public boolean isBound(Animation animation) {
            return action.isBound(animation);
        }

        public boolean stores(DelayedAction<T, ?> action) {
            return this.action.equals(action);
        }

        public boolean shouldBeExecuted(AnimationWatcher watcherWithBoundAnimation) {
            return !done && action.getActionDelayPredicate().test(watcherWithBoundAnimation);
        }

        public void runAction(T entity) {
            action.getAction().accept(entity, actionData);
            done = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActionWatcher)) return false;
            ActionWatcher<?, ?> that = (ActionWatcher<?, ?>) o;
            return action.equals(that.action);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action);
        }
    }
}
