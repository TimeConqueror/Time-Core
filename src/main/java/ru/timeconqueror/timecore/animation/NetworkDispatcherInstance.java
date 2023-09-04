package ru.timeconqueror.timecore.animation;

import lombok.AllArgsConstructor;
import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.internal.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.internal.common.packet.animation.CodecSupplier;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CEndAnimationMsg;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStartAnimationMsg;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CSyncAnimationsMsg;

import java.util.List;

@AllArgsConstructor
public class NetworkDispatcherInstance<T extends AnimatedObject<T>> {
    private final NetworkDispatcher<T> networkDispatcher;
    private final T animatedObject;

    public void sendSetAnimationPacket(AnimationStarter.AnimationData data, String layerName) {
        InternalPacketManager.INSTANCE.send(getPacketTarget(), new S2CStartAnimationMsg(getCodecSupplier(), layerName, data));
    }

    public void sendRemoveAnimationPacket(String layerName, int transitionTime) {
        InternalPacketManager.INSTANCE.send(getPacketTarget(), new S2CEndAnimationMsg(getCodecSupplier(), layerName, transitionTime));
    }

    public void sendSyncAnimationsPacket(List<Pair<String, AnimationTicker>> tickersByLayer) {
        InternalPacketManager.INSTANCE.send(getPacketTarget(), new S2CSyncAnimationsMsg(getCodecSupplier(), tickersByLayer));
    }

    public CodecSupplier getCodecSupplier() {
        return networkDispatcher.getCodec(animatedObject);
    }

    public PacketDistributor.PacketTarget getPacketTarget() {
        return networkDispatcher.getPacketTarget(animatedObject);
    }
}
