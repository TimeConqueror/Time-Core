package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.animation.action.ActionManager;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.Clock;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.LinkedHashMap;

public class ServerAnimationManager<T extends AnimatedObject<T>> extends BaseAnimationManager {
    private final NetworkDispatcherInstance<T> networkDispatcher;

    public ServerAnimationManager(Clock clock, SharedMolangObject sharedMolangObject, NetworkDispatcherInstance<T> networkDispatcher) {
        super(clock, sharedMolangObject);
        this.networkDispatcher = networkDispatcher;
    }

    @Override
    public void init(LinkedHashMap<String, LayerDefinition> layers) {
        super.init(layers);
        for (LayerImpl layer : this.getLayerMap().values()) {
            layer.addAnimationEventListener(new ActionManager(networkDispatcher.getAnimatedObject()));
        }
    }

    @Override
    public boolean startAnimation(AnimationData data, String layerName, AnimationCompanionData companionData) {
        var set = super.startAnimation(data, layerName, companionData);
        if (set) {
            networkDispatcher.sendSetAnimationPacket(data, layerName);
        }
        return set;
    }

    @Override
    public void stopAnimation(String layerName, int transitionTime) {
        super.stopAnimation(layerName, transitionTime);

        networkDispatcher.sendStopAnimationPacket(layerName, transitionTime);
    }
}
