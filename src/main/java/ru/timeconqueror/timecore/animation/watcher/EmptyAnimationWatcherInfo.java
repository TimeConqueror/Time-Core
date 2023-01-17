package ru.timeconqueror.timecore.animation.watcher;

import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;

public class EmptyAnimationWatcherInfo implements IAnimationWatcherInfo {
    @Override
    public int getExistingTime(long currentMillis) {
        return 0;
    }

    @Override
    public int getCurrentAnimationTime(long currentMillis) {
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

    @Override
    public float getSpeed() {
        return 0;
    }
}
