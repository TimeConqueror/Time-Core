package ru.timeconqueror.timecore.api.client.render.animation;

import ru.timeconqueror.timecore.client.render.animation.AnimationManageBuilder;
import ru.timeconqueror.timecore.client.render.animation.AnimationStarter;

public class AnimationAPI {
    public static AnimationManageBuilder newManagerFactory() {
        return new AnimationManageBuilder();
    }

    public static AnimationStarter newAnimationStarter(IAnimation animation) {
        return new AnimationStarter(animation);
    }
}
