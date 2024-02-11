package ru.timeconqueror.timecore.api.animation.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTicker;

@Getter
@AllArgsConstructor
public class ActionContext<T extends AnimatedObject<T>, DATA> {
    private final AnimationTicker ticker;
    private final T owner;
    private final DATA extraData;
    private final long clockTime;
    private final int lastAnimationCycleIndex;
}