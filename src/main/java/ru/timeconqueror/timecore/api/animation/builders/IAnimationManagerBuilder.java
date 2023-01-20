package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.api.animation.BlendType;

public interface IAnimationManagerBuilder {
    void addLayer(String name, BlendType blendType, float weight);

    void addLayer(LayerDefinition layer);

    void addMainLayer();
}
