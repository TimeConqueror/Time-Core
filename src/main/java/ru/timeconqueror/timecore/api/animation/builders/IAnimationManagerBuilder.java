package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.animation.Layer;
import ru.timeconqueror.timecore.api.animation.BlendType;

public interface IAnimationManagerBuilder {
    void addLayer(String name, BlendType blendType, float weight);

    void addLayer(Layer layer);

    void addMainLayer();
}
