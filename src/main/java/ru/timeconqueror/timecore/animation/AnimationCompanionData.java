package ru.timeconqueror.timecore.animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class AnimationCompanionData {
    public static final AnimationCompanionData EMPTY = new AnimationCompanionData(Collections.emptyList());

    @Getter
    private final List<ActionInstance<?, ?>> actionList;
}
