package ru.timeconqueror.timecore.client.render.animation;

import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationLayer;

public class AnimationStarter {
    private AnimationData data;
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
        data.speedFactor = Math.max(speedFactor, 0);
        return this;
    }

    public void startAt(IAnimationLayer layer) {
        if (this.ignorable && layer.hasAnimation() && data.prototype.equals(layer.getCurrentAnimation())) {
            return;
        }

        layer.setAnimation(this.data);
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
