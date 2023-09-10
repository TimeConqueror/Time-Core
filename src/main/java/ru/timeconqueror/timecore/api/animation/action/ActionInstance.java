package ru.timeconqueror.timecore.api.animation.action;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ActionInstance<T extends AnimatedObject<T>, DATA> {
    private final Action<T, DATA> action;
    private final DATA data;

    public static <T extends AnimatedObject<T>, DATA> ActionInstance<T, DATA> of(Action<T, DATA> action, DATA data) {
        return new ActionInstance<>(action, data);
    }

    public static <T extends AnimatedObject<T>> ActionInstance<T, Void> of(Action<T, Void> action) {
        return new ActionInstance<>(action, null);
    }
}
