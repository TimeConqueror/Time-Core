package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.predefined.PredefinedAnimation;

public interface PredefinedAnimationManager<T extends AnimatedObject<T>> {
    void onTick(AnimationSystem<T> animationSystem, T owner);

    default boolean sameLayers(PredefinedAnimation anim1, PredefinedAnimation anim2) {
        return anim1.getLayerName().equals(anim2.getLayerName());
    }
}
