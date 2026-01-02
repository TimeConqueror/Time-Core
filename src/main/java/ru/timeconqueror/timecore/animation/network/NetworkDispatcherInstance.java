package ru.timeconqueror.timecore.animation.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStartAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStopAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CSyncAnimationsPacket;

import java.util.List;

@AllArgsConstructor
public class NetworkDispatcherInstance<T extends AnimatedObject<T>> {
    private final NetworkDispatcher<T> networkDispatcher;
    @Getter
    private final T animatedObject;

    public void sendSetAnimationPacketToAllTracking(AnimationScript animationScript, String layerName) {
        networkDispatcher.send(animatedObject, new S2CStartAnimationPacket(getCodecSupplier(), layerName, animationScript));
    }

    public void sendStopAnimationPacketToAllTracking(String layerName, int transitionTime) {
        networkDispatcher.send(animatedObject, new S2CStopAnimationPacket(getCodecSupplier(), layerName, transitionTime));
    }

    public void sendSyncAnimationPacketToPlayer(ServerPlayer player, List<Pair<String, AnimationState>> statesByLayer) {
        PacketDistributor.sendToPlayer(player, new S2CSyncAnimationsPacket(getCodecSupplier(), statesByLayer));
    }

    protected LevelObjectCodec<?> getCodecSupplier() {
        return networkDispatcher.getCodec(animatedObject);
    }
}
