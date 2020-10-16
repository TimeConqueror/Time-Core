package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.api.animation.AnimationManager;

/**
 * Used in animation manager builder to determine in which layer animation will be played
 */
public class PredefinedAnimation {
    private final String layerName;
    private final AnimationStarter animationStarter;

    public PredefinedAnimation(String layerName, AnimationStarter animationStarter) {
        this.layerName = layerName;
        this.animationStarter = animationStarter;
    }

    public String getLayerName() {
        return layerName;
    }

    public AnimationStarter getAnimationStarter() {
        return animationStarter;
    }

    public void startAt(AnimationManager manager) {
        animationStarter.startAt(manager, layerName);
    }
}
