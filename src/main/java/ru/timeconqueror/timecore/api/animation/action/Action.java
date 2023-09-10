package ru.timeconqueror.timecore.api.animation.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTickerInfo;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public class Action<T extends AnimatedObject<T>, DATA> {
    @NonNull
    private final AnimationTickListener<T, DATA> listener;
    private final boolean repeatedOnLoop;

    public static <T extends AnimatedObject<T>, DATA> ActionBuilder<T, DATA> builder() {
        return new ActionBuilder<>();
    }

    public interface AnimationTickListener<T extends AnimatedObject<T>, DATA> {
        /**
         * Called on server every tick and one extra time upon animation end until this method returns true.
         * Here you can fully control the behavior of attached animated object.
         *
         * @param owner     bound animated object (entity, tile entity, etc.) for which action is currently active.
         * @param extraData user data to be read or written
         * @return true if the action is done
         */
        boolean onTick(AnimationTickerInfo ticker, T owner, DATA extraData);
    }

    public static class ActionBuilder<T extends AnimatedObject<T>, DATA> {
        private AnimationTickListener<T, DATA> listener;
        private boolean repeatedOnLoop;

        public ActionBuilder<T, DATA> onceRunListener(Predicate<AnimationTickerInfo> predicate, BiConsumer<? super T, DATA> listener) {
            this.listener = (ticker, owner, extraData) -> {
                if (predicate.test(ticker)) {
                    listener.accept(owner, extraData);
                    return true;
                }

                return false;
            };

            return this;
        }

        public ActionBuilder<T, DATA> everyTickListener(BiConsumer<? super T, ? super DATA> listener) {
            this.listener = (ticker, owner, extraData) -> {
                listener.accept(owner, extraData);

                return false;
            };

            return this;
        }

        public ActionBuilder<T, DATA> repeatedOnLoop(boolean repeatedOnLoop) {
            this.repeatedOnLoop = repeatedOnLoop;
            return this;
        }

        public ActionBuilder<T, DATA> listener(AnimationTickListener<T, DATA> listener) {
            this.listener = listener;
            return this;
        }

        public Action<T, DATA> build() {
            if (listener == null) throw new IllegalStateException("listener was not set");

            return new Action<>(listener, repeatedOnLoop);
        }
    }
}
