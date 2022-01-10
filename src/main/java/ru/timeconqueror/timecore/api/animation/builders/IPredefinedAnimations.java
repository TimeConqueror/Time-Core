package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.animation.AnimationStarter;

public interface IPredefinedAnimations {
    interface IEntityPredefinedAnimations extends IPredefinedAnimations {
        void setWalkingAnimation(AnimationStarter walkingAnimationStarter, String layerName);

        void setIdleAnimation(AnimationStarter idleAnimationStarter, String layerName);
    }
}