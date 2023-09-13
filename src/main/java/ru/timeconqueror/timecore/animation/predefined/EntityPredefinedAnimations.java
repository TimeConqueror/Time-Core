package ru.timeconqueror.timecore.animation.predefined;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Builder
@Data
@AllArgsConstructor
public class EntityPredefinedAnimations {
    @Nullable
    private PredefinedAnimation walkingAnimation;
    @Nullable
    private PredefinedAnimation idleAnimation;
}