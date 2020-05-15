package ru.timeconqueror.timecore.client.render.animation;

public class AnimationWatcher {
    private long startTime;
    private Animation animation;

    public AnimationWatcher(Animation animation) {
        this.animation = animation;
        resetTimer();
    }

    public boolean isAnimationEnded() {
        return System.currentTimeMillis() > startTime + animation.getLength();
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getExistingTime() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}
