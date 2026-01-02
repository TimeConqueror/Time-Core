package ru.timeconqueror.timecore.internal.common.packet.animation;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.network.AnimationState;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.common.packet.PayloadHelper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class S2CSyncAnimationsPacket extends S2CAnimationPacket {
    public static final Type<S2CSyncAnimationsPacket> TYPE = TimeCore.payloadType(S2CSyncAnimationsPacket.class);
    public static StreamCodec<RegistryFriendlyByteBuf, S2CSyncAnimationsPacket> STREAM_CODEC = PayloadHelper.simpleStreamCodec(S2CSyncAnimationsPacket::encode, S2CSyncAnimationsPacket::new);

    private final List<Pair<String, AnimationState>> statesByLayer;

    public S2CSyncAnimationsPacket(RegistryFriendlyByteBuf buf) {
        super(buf);
        int size = buf.readVarInt();
        var statesByLayer = new ArrayList<Pair<String, AnimationState>>(size);
        for (int i = 0; i < size; i++) {
            statesByLayer.add(Pair.of(buf.readUtf(), AnimationState.deserialize(buf)));
        }
        this.statesByLayer = statesByLayer;
    }

    public S2CSyncAnimationsPacket(LevelObjectCodec<?> codecSupplier, List<Pair<String, AnimationState>> statesByLayer) {
        super(codecSupplier);
        this.statesByLayer = statesByLayer;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeVarInt(statesByLayer.size());
        for (Pair<String, AnimationState> e : statesByLayer) {
            buf.writeUtf(e.left());
            e.right().serialize(buf);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
