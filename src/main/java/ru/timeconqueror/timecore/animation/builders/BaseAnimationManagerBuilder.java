package ru.timeconqueror.timecore.animation.builders;

import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.builders.IAnimationManagerBuilder;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.util.SingleUseBuilder;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.LinkedHashMap;

public class BaseAnimationManagerBuilder extends SingleUseBuilder implements IAnimationManagerBuilder {
    private final LinkedHashMap<String, LayerDefinition> layerDefinitions = new LinkedHashMap<>();

    @Override
    public void addLayer(String name, BlendType blendType, float weight) {
        addLayer(new LayerDefinition(name, blendType, weight));
    }

    @Override
    public void addMainLayer() {
        verifyNotUsed();
        addLayer(AnimationConstants.MAIN_LAYER_NAME, BlendType.OVERWRITE, 1);
    }

    @Override
    public void addLayer(LayerDefinition layerDefinition) {
        verifyNotUsed();
        if (layerDefinitions.put(layerDefinition.name(), layerDefinition) != null) {
            throw new IllegalArgumentException("Layer with location " + layerDefinition.name() + " already exist in provided animation manager.");
        }
    }

    <T extends AnimatedObject<T>> BaseAnimationManager build(boolean serverSide, AnimatedObjectType type, SharedMolangObject sharedMolangObject, NetworkDispatcherInstance<T> networkDispatcherInstance) {
        BaseAnimationManager manager;
        if (serverSide) {
            manager = new ServerAnimationManager<>(sharedMolangObject, networkDispatcherInstance);
        } else {
            manager = new ClientAnimationManager(sharedMolangObject);
        }

        if (layerDefinitions.isEmpty()) {
            addMainLayer();
        }

        manager.buildLayers(layerDefinitions);

        setUsed();

        return manager;
    }
}
