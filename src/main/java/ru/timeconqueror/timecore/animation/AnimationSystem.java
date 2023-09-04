package ru.timeconqueror.timecore.animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;

@AllArgsConstructor
@Getter
public class AnimationSystem<T extends AnimatedObject<T>> {
    private final ActionManager<T> actionManager;
    private final AnimationManager animationManager;
    private final NetworkDispatcherInstance<T> networkDispatcher;
}
