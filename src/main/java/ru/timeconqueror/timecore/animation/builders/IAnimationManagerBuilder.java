package ru.timeconqueror.timecore.animation.builders;

import ru.timeconqueror.timecore.api.animation.BlendType;

public interface IAnimationManagerBuilder {
    void addLayer(String name, BlendType blendType, float weight);

    void addMainLayer();
}
