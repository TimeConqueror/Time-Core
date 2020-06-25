package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DelayedAction<T extends Entity> {
    private final AnimationStarter animationStarter;
    private final String animationLayer;

    private Predicate<AnimationWatcher> actionDelayPredicate = StandardDelayPredicates.onStart();
    private Consumer<T> action = t -> {
    };

    private final ResourceLocation id;

    public DelayedAction(ResourceLocation id, AnimationStarter animationStarter, String animationLayer) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DelayedAction)) return false;
        DelayedAction<?> action = (DelayedAction<?>) o;
        return id.equals(action.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
