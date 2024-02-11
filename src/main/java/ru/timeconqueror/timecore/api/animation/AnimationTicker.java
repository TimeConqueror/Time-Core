package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.watcher.Timeline;

public interface AnimationTicker extends PlayingAnimationInfo {
    Timeline getTimeline();

    @Override
    default boolean isReversed() {
        return getTimeline().isReversed();
    }

    @Override
    default boolean isLooped() {
        return !isTransition() && !isEmpty() && getAnimationData().getLoopMode().isLooped();
    }

    boolean isEmpty();

    boolean isTransition();

    @Override
    default int getAnimationLength() {
        return getTimeline().getLength();
    }

    /**
     * Represents the progress within the animation length.
     * Returns the progress time, which may vary from 0 to animation length.
     * Being reversed, it goes backwards.
     *
     * @see #getElapsedTimeAt(long)
     */
    int getAnimationTimeAt(long clockTime);

    /**
     * Returns the amount of <b>real</b> time in milliseconds which is passed from the start of bound animation.
     *
     * @see #getAnimationTimeAt(long)
     */
    default int getElapsedTimeAt(long clockTime) {
        return getTimeline().getElapsedTime(clockTime);
    }
}
