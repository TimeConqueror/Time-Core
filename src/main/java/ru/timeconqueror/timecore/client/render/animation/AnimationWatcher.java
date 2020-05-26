package ru.timeconqueror.timecore.client.render.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.util.Requirements;

public class AnimationWatcher {
    private long startTime;
    private IAnimation animation;
    private TransitionData transitionData;

    /**
     * Speed factor of the animation
     */
    private float speed;

    public AnimationWatcher(IAnimation animation, float speed) {
        Requirements.greaterThan(speed, 0);
        this.animation = animation;
        this.speed = speed;
        resetTimer();
    }

    public boolean isAnimationEnded(long time) {
//        System.out.println("Current time: " + time);
//        System.out.println("Start time: " + startTime);
//        System.out.println("Difference:  " + (time - startTime));
//        System.out.println("Animation length: " + animation.getLength());
//        System.out.println("Calculated length: " + (animation.getLength() / speed));
//        System.out.println("Full time: " + (startTime + Math.round(animation.getLength() / speed)));
//        System.out.println("Is ended: " + (time > (startTime + Math.round(animation.getLength() / speed))));
        return time > startTime + Math.round(animation.getLength() / speed);
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
    }

    public IAnimation getAnimation() {
        return animation;
    }

    public int getExistingTime(long time) {
        return (int) ((int) (time - startTime) * speed);
    }

    public int getExistingTime() {
        return getExistingTime(System.currentTimeMillis());
    }

    public void enableTransitionMode(@Nullable IAnimation destination, int transitionTime, float speedFactor) {
        Requirements.greaterOrEqualsThan(transitionTime, 0);
        Requirements.greaterThan(speedFactor, 0);
        transitionData = new TransitionData(transitionTime, speedFactor, destination);
    }

    public boolean requiresTransitionPreparation() {
        return transitionData != null && !transitionData.transitionCreated;
    }

    public void initTransition(TimeEntityModel<?> model) {
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
        private int transitionTime;
        private float speedFactor;
        @Nullable
        private IAnimation destination;

        private boolean transitionCreated = false;

        public TransitionData(int transitionTime, float speedFactor, @Nullable IAnimation destination) {
            this.transitionTime = transitionTime;
            this.speedFactor = speedFactor;
            this.destination = destination;
        }

        public float getSpeedFactor() {
            return speedFactor;
        }
    }
}
