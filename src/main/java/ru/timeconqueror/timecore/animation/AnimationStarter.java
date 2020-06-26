package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationLayer;

public class AnimationStarter {
    private final AnimationData data;
    private boolean ignorable = true;

    public AnimationStarter(IAnimation animation) {
        this.data = new AnimationData(animation);
    }

    public AnimationStarter setIgnorable(boolean ignorable) {
        this.ignorable = ignorable;
        return this;
    }

    public AnimationStarter setTransitionTime(int transitionTime) {
        data.transitionTime = Math.max(transitionTime, 0);
        return this;
    }

    public AnimationStarter setSpeed(float speedFactor) {
        data.speedFactor = Math.max(speedFactor, 0.0001F);
        return this;
    }

    public void startAt(AnimationManager manager, String layerName) {
        if (manager.containsLayer(layerName)) {
            startAt(manager.getLayer(layerName));
        } else {
            TimeCore.LOGGER.error("Can't start animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }

    public void startAt(IAnimationLayer layer) {
        if (ignorable) {
            AnimationWatcher watcher = layer.getAnimationWatcher();
            if (watcher != null) {
                if (data.prototype.equals(watcher.getAnimation()) || (watcher instanceof TransitionWatcher && data.prototype.equals(((TransitionWatcher) watcher).getDestination()))) {
                    return;//TODO add check for speed
                }
            }
        }

        layer.setAnimation(this.data);
    }

    AnimationData getData() {
        return data;
    }

    public static class AnimationData {
        final IAnimation prototype;
        int transitionTime = AnimationConstants.BASIC_TRANSITION_TIME;
        float speedFactor = 1F;

        private AnimationData(IAnimation prototype) {
            this.prototype = prototype;
        }
    }
}
