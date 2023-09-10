package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.api.animation.BlendType;

public interface AnimationManagerBuilder {
    void addLayer(String name, BlendType blendType, float weight);

    void addLayer(LayerDefinition layer);

    void addMainLayer();
}
