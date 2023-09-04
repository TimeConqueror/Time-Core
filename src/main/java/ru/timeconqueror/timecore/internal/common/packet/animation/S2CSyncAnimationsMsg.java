package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.animation.util.TickerSerializers;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.util.holder.Pair;

import java.util.List;

public class S2CSyncAnimationsMsg extends S2CAnimationMsg {
    private final List<Pair<String, AnimationTicker>> tickersByLayer;

    public S2CSyncAnimationsMsg(CodecSupplier codecSupplier, List<Pair<String, AnimationTicker>> tickersByLayer) {
        super(codecSupplier);
        this.tickersByLayer = tickersByLayer;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CSyncAnimationsMsg> {
        @Override
        public void encodeExtra(S2CSyncAnimationsMsg packet, FriendlyByteBuf buffer) {
            TickerSerializers.serializeTickers(packet.tickersByLayer, buffer);
        }

        @Override
        public S2CSyncAnimationsMsg decodeWithExtraData(CodecSupplier codecSupplier, FriendlyByteBuf buffer) {
            var tickersByLayer = TickerSerializers.deserializeTickers(buffer);
            return new S2CSyncAnimationsMsg(codecSupplier, tickersByLayer);
        }

        @Override
        public void onPacket(S2CSyncAnimationsMsg packet, AnimatedObject<?> provider, NetworkEvent.Context ctx) {
            for (Pair<String, AnimationTicker> pair : packet.tickersByLayer) {
                provider.getAnimationManager().getLayer(pair.left()).setCurrentTicker(pair.right());
            }
        }
    }
}
