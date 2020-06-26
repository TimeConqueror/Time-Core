package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationLayer;
import ru.timeconqueror.timecore.api.util.MathUtils;

public class Layer implements IAnimationLayer {
    private int priority;
    private String name;

    @Nullable
    private AnimationWatcher animationWatcher;
    private BlendType blendType;
    private float weight;

    public Layer(String name, int priority, BlendType blendType, float weight) {
        this.name = name;
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
            animationWatcher = new TransitionWatcher(data.transitionTime, data.prototype, data.speedFactor);
        } else {
            animationWatcher = new TransitionWatcher(animationWatcher.getAnimation(), animationWatcher.getExistingTime(), data.transitionTime, data.prototype, data.speedFactor);
        }
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
                if (!(animationWatcher instanceof TransitionWatcher && ((TransitionWatcher) animationWatcher).getDestination() == null)) {
                    animationWatcher = new TransitionWatcher(animationWatcher.getAnimation(), animationWatcher.getExistingTime(), transitionTime, null, -1);
                }
            }
        }
    }

    public AnimationWatcher getAnimationWatcher() {
        return animationWatcher;
    }

    void setAnimationWatcher(AnimationWatcher animationWatcher) {
        this.animationWatcher = animationWatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Layer clone() throws CloneNotSupportedException {
        if (this.animationWatcher != null) {
            throw new CloneNotSupportedException("Can't clone this layer, it's already in work.");
        }

        Layer clone = (Layer) super.clone();
        clone.name = name;
        clone.priority = priority;
        clone.blendType = blendType;
        clone.weight = weight;

        return clone;
    }
}