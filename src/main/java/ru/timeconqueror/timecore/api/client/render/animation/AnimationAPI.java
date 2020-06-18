package ru.timeconqueror.timecore.api.client.render.animation;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;

public class AnimationAPI {
    public static AnimationStarter newAnimationStarter(IAnimation animation) {
        return new AnimationStarter(animation);
    }

    public static void removeAnimation(AnimationManager animationManager, String layerName) {
        if (animationManager.containsLayer(layerName)) {
            animationManager.getLayer(layerName).removeAnimation();
        } else {
            TimeCore.LOGGER.error("Can't remove animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }
}
