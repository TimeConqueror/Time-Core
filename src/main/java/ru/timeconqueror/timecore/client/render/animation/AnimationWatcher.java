package ru.timeconqueror.timecore.client.render.animation;

public class AnimationWatcher {
    private long startTime;
    private Animation animation;

    public AnimationWatcher(Animation animation) {
        this.animation = animation;
        startTime = System.currentTimeMillis();
    }

    public boolean isAnimationEnded() {
        return !animation.isLooped() && System.currentTimeMillis() > startTime + animation.getLength();
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getExistingTime() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}
