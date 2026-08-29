package ru.timeconqueror.timecore.animation;

import net.minecraft.server.level.ServerPlayer;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.api.animation.Clock;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

public class ServerAnimationManager<T extends AnimatedObject<T>> extends BaseAnimationManager {
    private final NetworkDispatcherInstance<T> networkDispatcher;
    private final boolean syncAnimationsOnFirstTick;
    private boolean firstTick = true;

    public ServerAnimationManager(Clock clock,
                                  SharedMolangObject sharedMolangObject,
                                  NetworkDispatcherInstance<T> networkDispatcher,
                                  boolean syncAnimationsOnFirstTick) {
        super(clock, sharedMolangObject);
        this.networkDispatcher = networkDispatcher;
        this.syncAnimationsOnFirstTick = syncAnimationsOnFirstTick;
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

    public void syncAnimations(ServerPlayer player) {
        networkDispatcher.sendSyncAnimationsPacketToPlayer(player, getLayerStates());
    }

    @Override
    public void tick() {
        if (firstTick) {
            firstTick = false;
            if (syncAnimationsOnFirstTick) {
                networkDispatcher.sendSyncAnimationsPacketToAllTracking(getLayerStates());
            }
        }

        super.tick();
    }
}
