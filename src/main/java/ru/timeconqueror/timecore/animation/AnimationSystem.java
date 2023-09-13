package ru.timeconqueror.timecore.animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationSystemAPI;
import ru.timeconqueror.timecore.api.animation.PredefinedAnimationManager;

@AllArgsConstructor(onConstructor = @__(@ApiStatus.Internal))
@Getter
public class AnimationSystem<T extends AnimatedObject<T>> {
    private final T owner;
    private final AnimationManager animationManager;
    private final NetworkDispatcherInstance<T> networkDispatcher;
    @Accessors(fluent = true)
    private final AnimationSystemAPI<T> api;
    private final PredefinedAnimationManager<T> predefinedAnimationManager;

    public void onTick(boolean clientSide) {
        predefinedAnimationManager.onTick(this, owner);
        if (!clientSide) {
            // simulate ticking
            animationManager.applyAnimations(null);
        }
    }
}
