package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.StateMachine;

import java.util.ArrayList;
import java.util.List;

public class StateMachineImpl<T extends MobEntity> implements StateMachine<T> {
    private final List<ActionWatcher<T>> actionWatchers = new ArrayList<>();
    private final BaseAnimationManager animationManager;
    private final T entity;

    public StateMachineImpl(BaseAnimationManager animationManager, T entity) {
        this.animationManager = animationManager;
        this.entity = entity;
    }

    @Override
    public void enableAction(DelayedAction<T> action) {
        actionWatchers.add(new ActionWatcher<>(action));
        action.getAnimationStarter().startAt(animationManager, action.getAnimationLayer());
    }

    public boolean isActionEnabled(DelayedAction<T> action) {
        for (ActionWatcher<T> actionWatcher : actionWatchers) {
            if (actionWatcher.getDelayedAction().equals(action)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void disableAction(DelayedAction<T> action) {
        actionWatchers.removeIf(watcher -> watcher.action.equals(action));

        animationManager.removeAnimation(action.getAnimationLayer());
    }

    @Override
    public void onTick() {
        if (entity.world.isRemote) {
            boolean posChanged = entity.posX != entity.prevPosX || entity.posY != entity.prevPosY || entity.posZ != entity.prevPosZ;
            if (animationManager.containsLayer(LayerReference.WALKING.getName())) {
                if (posChanged) {
                    if (animationManager.getWalkingAnimationStarter() != null) {
                        animationManager.getWalkingAnimationStarter().startAt(animationManager, LayerReference.WALKING.getName());
                    }
                } else {
                    animationManager.removeAnimation(LayerReference.WALKING.getName());
                }
            }

//            new AnimationStarter(animationManager.getWalkingAnimation())
//                    .setIgnorable(true)
//                    .startAt(animationManager.getLayer(LayerReference.WALKING.getName()));
//            AnimationAPI.removeAnimation(animationManager, "walking");
        }
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
        private final DelayedAction<T> action;

        public ActionWatcher(DelayedAction<T> action) {
            this.action = action;
        }

        public DelayedAction<T> getDelayedAction() {
            return action;
        }
    }
}
