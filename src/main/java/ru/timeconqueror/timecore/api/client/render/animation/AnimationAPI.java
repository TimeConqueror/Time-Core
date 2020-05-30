package ru.timeconqueror.timecore.api.client.render.animation;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.render.animation.AnimationManageBuilder;
import ru.timeconqueror.timecore.client.render.animation.AnimationStarter;

public class AnimationAPI {
    public static AnimationManageBuilder newManagerFactory() {
        return new AnimationManageBuilder();
    }

    public static AnimationStarter startAnimation(IAnimation animation) {
        return new AnimationStarter(animation);
    }

    public static void removeAnimation(IAnimationManager animationManager, String layerName) {
        if (animationManager.containsLayer(layerName)) {
            animationManager.getLayer(layerName).removeAnimation();
        } else {
            TimeCore.LOGGER.error("Can't remove animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }
}
