package ru.timeconqueror.timecore.animation;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.timeconqueror.timecore.animation.clock.TickBasedClock;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.*;

@Getter
public class AnimationSystem<T extends AnimatedObject<T>> {
    private final T owner;
    private final AnimationManager animationManager;
    private final NetworkDispatcherInstance<T> networkDispatcher;
    @Accessors(fluent = true)
    private final AnimationSystemAPI<T> api;
    private final Clock clock;
    private final PredefinedAnimationManager<T> predefinedAnimationManager;

    public AnimationSystem(T owner,
                           Clock clock,
                           AnimationManager animationManager,
                           NetworkDispatcherInstance<T> networkDispatcher,
                           PredefinedAnimationManager<T> predefinedAnimationManager) {
        this.owner = owner;
        this.clock = clock;
        this.animationManager = animationManager;
        this.networkDispatcher = networkDispatcher;
        this.api = new AnimationSystemAPI<>(this);
        this.predefinedAnimationManager = predefinedAnimationManager;
    }

    public void onTick(boolean clientSide) {
        predefinedAnimationManager.onTick(this, owner);

        if (clock instanceof TickBasedClock tickBasedClock) {
            tickBasedClock.tick();
        }

        if (!clientSide) {
            // simulate ticking
            animationManager.applyAnimations(null, 0);
        }
    }
}
