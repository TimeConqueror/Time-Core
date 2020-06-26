package ru.timeconqueror.timecore.api.client.render.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationWatcher;

public interface AnimationLayer extends Cloneable {
    float getWeight();

    void setWeight(float weight);

    BlendType getBlendType();

    void setBlendType(BlendType type);

    @Nullable
    IAnimation getCurrentAnimation();

    boolean hasAnimation();

    @Nullable
    AnimationWatcher getAnimationWatcher();

    String getName();
}
