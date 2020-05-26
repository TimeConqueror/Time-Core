package ru.timeconqueror.timecore.api.client.render.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.animation.AnimationStarter;

public interface IAnimationLayer {
    float getWeight();

    void setWeight(float weight);

    BlendType getBlendType();

    void setBlendType(BlendType type);

    @Nullable
    IAnimation getCurrentAnimation();

    boolean hasAnimation();

    void setAnimation(AnimationStarter.AnimationData data);

    void removeAnimation();

    void removeAnimation(int transition);
}
