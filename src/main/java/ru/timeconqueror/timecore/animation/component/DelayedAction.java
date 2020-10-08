package ru.timeconqueror.timecore.animation.component;

import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.util.StandardDelayPredicates;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

//TODO add multiple runnables with single or continuing action and merge delay predicates with runnable
public class DelayedAction<T, EXTRA_DATA> {
    private final AnimationStarter animationStarter;
    private final String animationLayer;
    private final ResourceLocation id;
    private Predicate<AnimationWatcher> actionDelayPredicate = StandardDelayPredicates.onStart();
    private BiConsumer<? super T, ? super EXTRA_DATA> action = (entity, data) -> {
    };

    /**
     * @param id               ID of action. By this ID they will be compared for deletion and addition.
     * @param animationStarter animation, which will be played when action is started.
     * @param animationLayer   layer, where animation will be played.
     */
    public DelayedAction(ResourceLocation id, AnimationStarter animationStarter, String animationLayer) {
        this.id = id;
        this.animationStarter = animationStarter;
        this.animationLayer = animationLayer;
    }

    /**
     * Sets task, that will be run, when the predicate from {@link #setDelayPredicate(Predicate)} will return true.
     * <p>
     * Default: () -> {}.
     * <p>
     * So far, task from setOnCall can only be played once per animation.
     */
    public DelayedAction<T, EXTRA_DATA> setOnCall(BiConsumer<? super T, ? super EXTRA_DATA> action) {
        this.action = action;

        return this;
    }

    /**
     * Determines in which moment of time (relatively to the playing animation, in milliseconds) task,
     * which is set by {@link #setOnCall(BiConsumer)} will be run.
     * <p>
     * Default: {@link StandardDelayPredicates#onStart()}.
     *
     * @see StandardDelayPredicates
     */
    public DelayedAction<T, EXTRA_DATA> setDelayPredicate(Predicate<AnimationWatcher> delayPredicate) {
        this.actionDelayPredicate = delayPredicate;

        return this;
    }

    public BiConsumer<? super T, ? super EXTRA_DATA> getAction() {
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
        DelayedAction<?, ?> action = (DelayedAction<?, ?>) o;
        return id.equals(action.id);
    }

    public boolean isBound(Animation animation) {
        return animationStarter.getData().getAnimation().equals(animation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}