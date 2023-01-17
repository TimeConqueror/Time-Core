package ru.timeconqueror.timecore.api.animation.action;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.action.NewDelayedAction;
import ru.timeconqueror.timecore.animation.util.StandardDelayPredicates;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.animation.action.IDelayedAction.Handler;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Builder<T, EXTRA_DATA> {
    private final String id;
    private final String layer;
    private final AnimationStarter starter;
    private boolean repeatedOnLoop;
    private Handler<? super T, ? super EXTRA_DATA> handler;

    protected Builder(String id, String layer, AnimationStarter starter) {
        this.id = id;
        this.layer = layer;
        this.starter = starter;
    }

    /**
     * If set, then action will be restarted if bound looped animation is restarted
     */
    public Builder<T, EXTRA_DATA> repeatOnLoop() {
        this.repeatedOnLoop = true;
        return this;
    }

    /**
     * Sets the handler with full control access.
     */
    public Builder<T, EXTRA_DATA> withHandler(Handler<? super T, ? super EXTRA_DATA> handler) {
        this.handler = handler;
        return this;
    }

    /**
     * Sets the simple handler for action.
     * Action, which is provided in action parameter, will be only called once, when delay predicate parameter returns true.
     *
     * @param delayPredicate predicate, which should return true, when action is needed to be run.
     * @param action         action, which will be run once when delayPredicate will return true.
     * @see StandardDelayPredicates
     */
    public Builder<T, EXTRA_DATA> withSimpleHandler(Predicate<IAnimationWatcherInfo> delayPredicate, BiConsumer<? super T, ? super EXTRA_DATA> action) {
        this.handler = (watcher, object, extraData) -> {
            if (delayPredicate.test(watcher)) {
                action.accept(object, extraData);
                return true;
            }

            return false;
        };

        return this;
    }

    public IDelayedAction<T, EXTRA_DATA> build() {
        return new NewDelayedAction<>(id, layer, starter, handler != null ? handler : NewDelayedAction.emptyHandler(), repeatedOnLoop);
    }
}