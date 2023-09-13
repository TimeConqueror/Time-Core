package ru.timeconqueror.timecore.animation.predefined;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.PredefinedAnimationManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyPredefinedAnimationManager<T extends AnimatedObject<T>> implements PredefinedAnimationManager<T> {
    private static final EmptyPredefinedAnimationManager<?> EMPTY = new EmptyPredefinedAnimationManager<>();

    @SuppressWarnings("unchecked")
    public static <T extends AnimatedObject<T>> EmptyPredefinedAnimationManager<T> empty() {
        return (EmptyPredefinedAnimationManager<T>) EMPTY;
    }

    @Override
    public void onTick(AnimationSystem<T> animationSystem, T owner) {
    }
}
