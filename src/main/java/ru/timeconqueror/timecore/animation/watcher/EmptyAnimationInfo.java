package ru.timeconqueror.timecore.animation.watcher;

import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.IAnimationInfo;

public class EmptyAnimationInfo implements IAnimationInfo {
    @Override
    public int getExistingTime() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Animation getAnimation() {
        return Animation.NULL;
    }

    @Override
    public boolean isAutoTransition() {
        return false;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean autoTransitsTo(Animation animation) {
        return false;
    }

    @Override
    public boolean autoTransitsFrom(Animation animation) {
        return false;
    }
}
