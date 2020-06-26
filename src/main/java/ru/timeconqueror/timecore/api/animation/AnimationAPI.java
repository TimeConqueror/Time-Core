package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;

public class AnimationAPI {
    public static AnimationStarter newAnimationStarter(IAnimation animation) {
        return new AnimationStarter(animation);
    }

    public static void removeAnimation(AnimationManager animationManager, String layerName) {
        animationManager.removeAnimation(layerName);
    }

    //TODO add load model and anims
}
