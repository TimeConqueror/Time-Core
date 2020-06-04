package ru.timeconqueror.timecore.client.render.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.api.util.Requirements;

public class AnimationWatcher {
    private final FreezableTime startTime;
    private IAnimation animation;
    private TransitionData transitionData;

    /**
     * Speed factor of the animation
     */
    private final float speed;

    public AnimationWatcher(IAnimation animation, float speed) {
        Requirements.greaterThan(speed, 0);
        this.startTime = new FreezableTime(System.currentTimeMillis());
        this.animation = animation;
        this.speed = speed;
    }

    public boolean isAnimationEnded(long time) {
        return time > startTime.get() + Math.round(animation.getLength() / speed);
    }

    public void resetTimer() {
        startTime.set(System.currentTimeMillis());
    }

    public IAnimation getAnimation() {
        return animation;
    }

    public int getExistingTime(long time) {
        int i = (int) ((int) (time - (startTime.get())) * speed);
        return i;
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

    public boolean isInTransitionMode() {
        return transitionData != null;
    }

    void enableTransitionMode(@Nullable IAnimation destination, int transitionTime, float speedFactor) {
        Requirements.greaterOrEqualsThan(transitionTime, 0);
        Requirements.greaterThan(speedFactor, 0);
        transitionData = new TransitionData(transitionTime, speedFactor, destination);
    }

    boolean requiresTransitionPreparation() {
        return transitionData != null && !transitionData.transitionCreated;
    }

    void initTransition(TimeEntityModel<?> model) {
        AnimationWatcher.TransitionData transitionData = getTransitionData();
        //noinspection ConstantConditions (#requiresTransitionPreparation already check for having transition data)
        animation = Transition.create(this, transitionData.destination, model.getBaseModel(), transitionData.transitionTime);
        transitionData.transitionCreated = true;
    }

    @Nullable
    public TransitionData getTransitionData() {
        return transitionData;
    }

    public static class TransitionData {
        private final int transitionTime;
        private final float speedFactor;
        @Nullable
        private final IAnimation destination;

        private boolean transitionCreated = false;

        public TransitionData(int transitionTime, float speedFactor, @Nullable IAnimation destination) {
            this.transitionTime = transitionTime;
            this.speedFactor = speedFactor;
            this.destination = destination;
        }

        public float getSpeedFactor() {
            return speedFactor;
        }

        public int getTransitionTime() {
            return transitionTime;
        }

        @Nullable
        public IAnimation getDestination() {
            return destination;
        }
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
