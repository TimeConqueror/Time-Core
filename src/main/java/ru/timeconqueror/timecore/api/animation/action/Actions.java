package ru.timeconqueror.timecore.api.animation.action;

import ru.timeconqueror.timecore.animation.watcher.Timeline;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTicker;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.function.BiConsumer;

public class Actions {

    public static <T extends AnimatedObject<T>, DATA> AnimationUpdateListener<T, DATA> everyCycleAt(int animationTime, BiConsumer<? super T, DATA> listener) {
        return ctx -> onEveryCycleAt(ctx, animationTime, listener);
    }

    public static <T extends AnimatedObject<T>, DATA> AnimationUpdateListener<T, DATA> everyCycleAtPercents(float animationPercents, BiConsumer<? super T, DATA> listener) {
        return ctx -> {
            AnimationTicker ticker = ctx.getTicker();
            Timeline timeline = ticker.getTimeline();
            var animationTime = Math.round(timeline.getLength() * animationPercents);
            return onEveryCycleAt(ctx, animationTime, listener);
        };
    }

    public static <T extends AnimatedObject<T>, DATA> AnimationUpdateListener<T, DATA> onBoundaryEnd(BiConsumer<? super T, DATA> listener) {
        return Actions.<T, DATA>everyCycleAtPercents(1, listener);
    }

    public static <T extends AnimatedObject<T>, DATA> AnimationUpdateListener<T, DATA> onBoundaryStart(BiConsumer<? super T, DATA> listener) {
        return Actions.<T, DATA>everyCycleAtPercents(0, listener);
    }

    private static <T extends AnimatedObject<T>, DATA> int onEveryCycleAt(ActionContext<T, DATA> ctx, int animationTime, BiConsumer<? super T, DATA> listener) {
        AnimationTicker ticker = ctx.getTicker();
        Timeline timeline = ticker.getTimeline();
        animationTime = MathUtils.coerceInRange(animationTime, 0, timeline.getLength());

        int lastCycleIndex = ctx.getLastAnimationCycleIndex();
        long clockTime = ctx.getClockTime();

        long currentCycleIndex = timeline.getCycleIndex(clockTime);

        // > - means the action already happened on this cycle
        while (lastCycleIndex <= currentCycleIndex) {
            if (lastCycleIndex < currentCycleIndex) {
                listener.accept(ctx.getOwner(), ctx.getExtraData());
                lastCycleIndex++;
            }

            if (lastCycleIndex == currentCycleIndex) {
                var reached = timeline.isAnimationTimeReachedOnCurrentCycle(clockTime, animationTime);
                if (reached) {
                    listener.accept(ctx.getOwner(), ctx.getExtraData());
                    lastCycleIndex++;
                }
                break;
            }
        }

        return lastCycleIndex;
    }
}
