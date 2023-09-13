package ru.timeconqueror.timecore.animation.predefined;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import ru.timeconqueror.timecore.api.animation.AnimationStarter;

@Getter
@AllArgsConstructor
@Builder
public class PredefinedAnimation {
    @NonNull
    private final String layerName;
    @NonNull
    private final AnimationStarter starter;
}
