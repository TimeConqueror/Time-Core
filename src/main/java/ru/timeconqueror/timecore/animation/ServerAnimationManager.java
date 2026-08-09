package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.animation.action.LayerActionManager;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.api.animation.Clock;
import ru.timeconqueror.timecore.api.animation.LayerDefinition;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.List;
import java.util.function.Supplier;

public class ServerAnimationManager<T extends AnimatedObject<T>> extends BaseAnimationManager {
    private final NetworkDispatcherInstance<T> networkDispatcher;

    public ServerAnimationManager(Clock clock,
                                  SharedMolangObject sharedMolangObject,
                                  NetworkDispatcherInstance<T> networkDispatcher) {
        super(clock, sharedMolangObject);
        this.networkDispatcher = networkDispatcher;
    }

    @Override
    public boolean startAnimationScript(AnimationScript animationScript, String layerName) {
        var set = super.startAnimationScript(animationScript, layerName);
        if (set) {
            networkDispatcher.sendSetAnimationPacketToAllTracking(animationScript, layerName);
        }
        return set;
    }

    @Override
    public void stopAnimation(String layerName, int transitionTime) {
        super.stopAnimation(layerName, transitionTime);

        networkDispatcher.sendStopAnimationPacketToAllTracking(layerName, transitionTime);
    }
}
