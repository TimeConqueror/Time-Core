package ru.timeconqueror.timecore.api.animation.action;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ActionInstance<T extends AnimatedObject<T>, DATA> {
    private final Action<? super T, DATA> action;
    private final DATA data;

    public static <T extends AnimatedObject<T>, DATA> ActionInstance<? super T, DATA> of(Action<? super T, DATA> action, DATA data) {
        //noinspection Convert2Diamond
        return new ActionInstance<T, DATA>(action, data);
    }

    public static <T extends AnimatedObject<T>> ActionInstance<T, Void> of(Action<T, Void> action) {
        return new ActionInstance<>(action, null);
    }
}
