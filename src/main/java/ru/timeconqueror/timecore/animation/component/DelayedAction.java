package ru.timeconqueror.timecore.animation.component;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.action.IDelayedAction;

public class DelayedAction<T, EXTRA_DATA> implements IDelayedAction<T, EXTRA_DATA> {
    @SuppressWarnings("rawtypes")
    private static final Handler EMPTY_HANDLER = (watcher, object, o) -> true;

    protected final String id;
    protected final AnimationStarter starter;
    protected final String layer;
    protected final Handler<? super T, ? super EXTRA_DATA> handler;
    protected final boolean repeatedOnLoop;

    public DelayedAction(String id, String layer, AnimationStarter starter, Handler<? super T, ? super EXTRA_DATA> handler, boolean repeatedOnLoop) {
        this.id = id;
        this.starter = starter;
        this.layer = layer;
        this.handler = handler;
        this.repeatedOnLoop = repeatedOnLoop;
    }

    public boolean isBound(Animation animation) {
        return getStarter().getData().getAnimation().equals(animation);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public AnimationStarter getStarter() {
        return starter;
    }

    @Override
    public String getLayerName() {
        return layer;
    }

    @Override
    public Handler<? super T, ? super EXTRA_DATA> getHandler() {
        return handler;
    }

    @Override
    public boolean isRepeatedOnLoop() {
        return repeatedOnLoop;
    }

    @SuppressWarnings("unchecked")
    public static <T, EXTRA_DATA> Handler<T, EXTRA_DATA> emptyHandler() {
        return EMPTY_HANDLER;
    }
}