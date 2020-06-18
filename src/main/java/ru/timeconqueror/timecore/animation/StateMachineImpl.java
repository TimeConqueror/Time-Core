package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationAPI;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationManager;

import java.util.ArrayList;
import java.util.List;

public class StateMachineImpl<T extends Entity> implements StateMachine<T> {
    private final List<ActionWatcher<T>> actionWatchers = new ArrayList<>();
    private final BaseAnimationManager animationManager;
    private final T entity;

    public StateMachineImpl(BaseAnimationManager animationManager, T entity) {
        this.animationManager = animationManager;
        this.entity = entity;
    }

    @Override
    public void enableAction(Action<T> action, boolean looped) {
        actionWatchers.add(new ActionWatcher<>(action, looped));
        action.getAnimationStarter().startAt(animationManager, action.getAnimationLayer());
    }

    @Override
    public void disableAction(Action<T> action) {
        actionWatchers.removeIf(watcher -> watcher.action == action);

        AnimationAPI.removeAnimation(animationManager, action.getAnimationLayer());
    }

    @Override
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public List<ActionWatcher<T>> getActionWatchers() {
        return actionWatchers;
    }

    public T getEntity() {
        return entity;
    }

    public static class ActionWatcher<T extends Entity> {
        private final Action<T> action;
        private final boolean looped;

        public ActionWatcher(Action<T> action, boolean looped) {
            this.action = action;
            this.looped = looped;
        }

        public boolean isLooped() {
            return looped;
        }

        public Action<T> getAction() {
            return action;
        }
    }
}
