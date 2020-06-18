package ru.timeconqueror.timecore.animation;

import java.util.function.Predicate;

public class StandardDelayPredicates {
    public static Predicate<AnimationWatcher> onStart() {
        return watcher -> true;
    }

    public static Predicate<AnimationWatcher> onEnd() {
        return watcher -> watcher.getExistingTime() >= watcher.getAnimation().getLength();
    }

    public static Predicate<AnimationWatcher> whenPassed(int animationTime) {
        return watcher -> watcher.getExistingTime() >= animationTime;
    }

    public static Predicate<AnimationWatcher> unlessPassed(int animationTime) {
        return watcher -> watcher.getExistingTime() < animationTime;
    }
}
