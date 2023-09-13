package ru.timeconqueror.timecore.api.animation.builders;

import ru.timeconqueror.timecore.api.animation.BlendType;

public record LayerDefinition(String name, BlendType blendType, float weight) {
    public LayerDefinition(String name, BlendType blendType, float weight) {
        this.name = name;
        this.blendType = blendType;
        this.weight = weight;
    }
}