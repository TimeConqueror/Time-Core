package ru.timeconqueror.timecore.animation.component;

import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.action.NewDelayedAction;
import ru.timeconqueror.timecore.animation.util.StandardDelayPredicates;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Deprecated // for removal 1.18+, use IDelayedAction#builder()
public class DelayedAction<T, EXTRA_DATA> extends NewDelayedAction<T, EXTRA_DATA> {
    private Predicate<IAnimationWatcherInfo> actionDelayPredicate = StandardDelayPredicates.onStart();
    private BiConsumer<? super T, ? super EXTRA_DATA> action = (entity, data) -> {
    };

    private final Handler<T, EXTRA_DATA> handler = makeHandler();

    @Deprecated // 1.18 removal, use string id instead of resource location
    public DelayedAction(ResourceLocation id, AnimationStarter animationStarter, String animationLayer) {
        this(id.toString(), animationStarter, animationLayer);
    }

    public DelayedAction(String id, AnimationStarter animationStarter, String animationLayer) {
        super(id, animationLayer, animationStarter, emptyHandler(), false);
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
    public DelayedAction<T, EXTRA_DATA> setDelayPredicate(Predicate<IAnimationWatcherInfo> delayPredicate) {
        this.actionDelayPredicate = delayPredicate;

        return this;
    }

    public BiConsumer<? super T, ? super EXTRA_DATA> getAction() {
        return action;
    }

    public Predicate<IAnimationWatcherInfo> getActionDelayPredicate() {
        return actionDelayPredicate;
    }

    @Override
    public Handler<T, EXTRA_DATA> getHandler() {
        return handler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DelayedAction)) return false;
        DelayedAction<?, ?> action = (DelayedAction<?, ?>) o;
        return getId().equals(action.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private Handler<T, EXTRA_DATA> makeHandler() {
        return (watcher, obj, data) -> {
            if (getActionDelayPredicate().test(watcher)) {
                getAction().accept(obj, data);
                return true;
            }

            return false;
        };
    }
}