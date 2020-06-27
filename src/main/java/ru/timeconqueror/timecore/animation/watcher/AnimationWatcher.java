package ru.timeconqueror.timecore.animation.watcher;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.api.util.Requirements;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class AnimationWatcher {
    protected final FreezableTime startTime;
    /**
     * Speed factor of the animation
     */
    protected final float speed;
    private boolean inited = false;
    protected Animation animation;

    public AnimationWatcher(Animation animation, float speed) {
        Requirements.greaterThan(speed, 0);
        this.startTime = new FreezableTime(System.currentTimeMillis());
        this.animation = animation;
        this.speed = speed;
    }

    public boolean requiresInit() {
        return !inited;
    }

    @OverridingMethodsMustInvokeSuper
    public void init(TimeEntityModel<?> model) {
        inited = true;
    }

    @Nullable
    public AnimationWatcher next() {
        if (getAnimation().isLooped()) {
            resetTimer();

            return this;
        } else {
            return new TransitionWatcher(getAnimation(), getExistingTime(), AnimationConstants.BASIC_TRANSITION_TIME, null, -1);
        }
    }

    public boolean isAnimationEnded(long time) {
        return time > startTime.get() + Math.round(animation.getLength() / speed);
    }

    public void resetTimer() {
        startTime.set(System.currentTimeMillis());
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getExistingTime(long time) {
        return (int) ((int) (time - (startTime.get())) * speed);
    }

    public int getExistingTime() {
        return getExistingTime(System.currentTimeMillis());
    }

    public void freeze() {
        startTime.freeze();
    }

    public void unfreeze() {
        startTime.unfreeze();
    }

    private static class FreezableTime {
        private long time;
        private long freezingTime = -1;

        public FreezableTime(long time) {
            this.time = time;
        }

        public void freeze() {
            if (freezingTime == -1) {
                freezingTime = System.currentTimeMillis();
            }
        }

        public void unfreeze() {
            if (freezingTime != -1) {
                time += System.currentTimeMillis() - freezingTime;
                freezingTime = -1;
            }
        }

        public long get() {
            if (freezingTime != -1) {
                return time + (System.currentTimeMillis() - freezingTime);
            } else {
                return time;
            }
        }

        public void set(long time) {
            this.time = time;
        }
    }
}
