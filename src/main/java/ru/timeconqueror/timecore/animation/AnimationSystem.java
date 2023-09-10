package ru.timeconqueror.timecore.animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationSystemAPI;

@AllArgsConstructor(onConstructor = @__(@ApiStatus.Internal))
@Getter
public class AnimationSystem<T extends AnimatedObject<T>> {
    private final AnimationManager animationManager;
    private final NetworkDispatcherInstance<T> networkDispatcher;
    @Accessors(fluent = true)
    private final AnimationSystemAPI<T> api;
}
