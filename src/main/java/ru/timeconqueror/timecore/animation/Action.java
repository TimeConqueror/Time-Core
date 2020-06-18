package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Action<T extends Entity> {
    private final AnimationStarter animationStarter;
    private final String animationLayer;

    private Predicate<AnimationWatcher> actionDelayPredicate = StandardDelayPredicates.onStart();
    private Consumer<T> action = t -> {
    };

    public Action(AnimationStarter animationStarter, String animationLayer) {
        this.animationStarter = animationStarter;
        this.animationLayer = animationLayer;
    }

    public void setOnCall(Consumer<T> action) {
        this.action = action;
    }

    public void setDelayPredicate(Predicate<AnimationWatcher> delayPredicate) {
        this.actionDelayPredicate = delayPredicate;
    }

    public Consumer<T> getAction() {
        return action;
    }

    public AnimationStarter getAnimationStarter() {
        return animationStarter;
    }

    public Predicate<AnimationWatcher> getActionDelayPredicate() {
        return actionDelayPredicate;
    }

    public String getAnimationLayer() {
        return animationLayer;
    }
}
