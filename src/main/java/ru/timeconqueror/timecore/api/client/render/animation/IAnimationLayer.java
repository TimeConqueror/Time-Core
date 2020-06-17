package ru.timeconqueror.timecore.api.client.render.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.common.AnimationStarter;
import ru.timeconqueror.timecore.animation.common.AnimationWatcher;

public interface IAnimationLayer {
    float getWeight();

    void setWeight(float weight);

    BlendType getBlendType();

    void setBlendType(BlendType type);

    @Nullable
    IAnimation getCurrentAnimation();

    boolean hasAnimation();

    @Nullable
    AnimationWatcher getAnimationWatcher();

    void setAnimation(AnimationStarter.AnimationData data);

    void removeAnimation();

    void removeAnimation(int transition);
}
