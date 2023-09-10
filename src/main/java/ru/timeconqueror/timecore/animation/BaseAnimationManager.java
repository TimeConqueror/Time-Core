package ru.timeconqueror.timecore.animation;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseAnimationManager implements AnimationManager {
    @Getter
    private final SharedMolangObject sharedMolangObjects;
    @Getter(AccessLevel.PROTECTED)
    private Map<String, LayerImpl> layerMap;

    public BaseAnimationManager(SharedMolangObject sharedMolangObjects) {
        this.sharedMolangObjects = sharedMolangObjects;
    }

    public void init(LinkedHashMap<String, LayerDefinition> layers) {
        layerMap = layers.values().stream()
                .map(layerDefinition -> new LayerImpl(this, layerDefinition))
                .collect(Collectors.toMap(LayerImpl::getName, layer -> layer, (o, o2) -> o, LinkedHashMap::new));
    }

    @Override
    public boolean containsLayer(String name) {
        return layerMap.get(name) != null;
    }

    @NotNull
    @Override
    public LayerImpl getLayer(String name) {
        LayerImpl layer = layerMap.get(name);
        if (layer == null) throw new RuntimeException("There is no layer with location " + name);
        return layer;
    }

    @Override
    public Set<String> getLayerNames() {
        return layerMap.keySet();
    }

    @Override
    public boolean startAnimation(AnimationData data, String layerName, AnimationCompanionData companionData) {
        if (containsLayer(layerName)) {
            LayerImpl layer = getLayer(layerName);
            return layer.start(data, companionData);
        }

        TimeCore.LOGGER.error("Can't start animation: layer with location " + layerName + " doesn't exist in provided animation manager.");
        return false;
    }

    @Override
    public void stopAnimation(String layerName, int transitionTime) {
        if (containsLayer(layerName)) {
            getLayer(layerName).removeAnimation(transitionTime);
        } else {
            TimeCore.LOGGER.error("Can't find layer with location " + layerName);
        }
    }

    @Override
    public void applyAnimations(ITimeModel model) {
        long systemTime = System.currentTimeMillis();
        for (LayerImpl layer : layerMap.values()) {
            layer.update(this, systemTime);
            applyAnimation(model, layer, systemTime);
        }
    }

    protected abstract void applyAnimation(ITimeModel model, LayerImpl layer, long systemTime);

    protected abstract boolean isGamePaused();
}
