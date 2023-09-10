package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.animation.util.TickerSerializers;
import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.util.holder.Pair;

import java.util.List;

public class S2CSyncAnimationsMsg extends S2CAnimationMsg {
    private final List<Pair<String, AbstractAnimationTicker>> tickersByLayer;

    public S2CSyncAnimationsMsg(LevelObjectCodec<?> codecSupplier, List<Pair<String, AbstractAnimationTicker>> tickersByLayer) {
        super(codecSupplier);
        this.tickersByLayer = tickersByLayer;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CSyncAnimationsMsg> {
        @Override
        public void encodeExtra(S2CSyncAnimationsMsg packet, FriendlyByteBuf buffer) {
            TickerSerializers.serializeTickers(packet.tickersByLayer, buffer);
        }

        @Override
        public S2CSyncAnimationsMsg decodeWithExtraData(LevelObjectCodec<?> codecSupplier, FriendlyByteBuf buffer) {
            var tickersByLayer = TickerSerializers.deserializeTickers(buffer);
            return new S2CSyncAnimationsMsg(codecSupplier, tickersByLayer);
        }

        @Override
        public void onPacket(S2CSyncAnimationsMsg packet, AnimatedObject<?> owner, NetworkEvent.Context ctx) {
            for (Pair<String, AbstractAnimationTicker> pair : packet.tickersByLayer) {
                ((LayerImpl) owner.getSystem().getAnimationManager().getLayer(pair.left())).setCurrentTicker(pair.right());
            }
        }
    }
}
