package ru.timeconqueror.timecore.animation.common;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationLayer;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.animation.AnimationConstants;

public class Layer implements IAnimationLayer {
    private final int priority;

    @Nullable
    private AnimationWatcher animationWatcher;
    private BlendType blendType;
    private float weight;

    public Layer(int priority, BlendType blendType, float weight) {
        this.weight = MathUtils.coerceInRange(weight, 0, 1);
        this.blendType = blendType;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public BlendType getBlendType() {
        return blendType;
    }

    @Override
    public void setBlendType(BlendType type) {
        blendType = type;
    }

    @Override
    public @Nullable IAnimation getCurrentAnimation() {
        return hasAnimation() ? getAnimationWatcher().getAnimation() : null;
    }

    @Override
    public boolean hasAnimation() {
        return getAnimationWatcher() != null && getAnimationWatcher().getAnimation() != null;
    }

    @Override
    public void setAnimation(AnimationStarter.AnimationData data) {
        if (animationWatcher == null) {
            animationWatcher = new AnimationWatcher(null, data.speedFactor);
        }

        animationWatcher.enableTransitionMode(data.prototype, data.transitionTime, data.speedFactor);
    }

    @Override
    public void removeAnimation() {
        removeAnimation(AnimationConstants.BASIC_TRANSITION_TIME);
    }

    @Override
    public void removeAnimation(int transitionTime) {
        if (animationWatcher != null) {
            if (transitionTime == 0) {
                animationWatcher = null;
            } else {
                animationWatcher.enableTransitionMode(null, transitionTime, 1.0F);
            }
        }
    }

    public AnimationWatcher getAnimationWatcher() {
        return animationWatcher;
    }

    void setAnimationWatcher(AnimationWatcher animationWatcher) {
        this.animationWatcher = animationWatcher;
    }
}