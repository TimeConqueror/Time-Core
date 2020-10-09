package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.animation.AnimationStarter;

public interface IPredefinedAnimations {
    interface IEntityPredefinedAnimations extends IPredefinedAnimations {//TODO add idle animation

        void setWalkingAnimation(AnimationStarter walkingAnimationStarter, String layerName);
    }
}