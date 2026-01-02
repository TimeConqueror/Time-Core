package ru.timeconqueror.timecore.internal.common.packet.animation;

import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.common.packet.PayloadHelper;

@Getter
public class S2CStopAnimationPacket extends S2CAnimationPacket {
    public static final Type<S2CStopAnimationPacket> TYPE = TimeCore.payloadType(S2CStopAnimationPacket.class);
    public static StreamCodec<RegistryFriendlyByteBuf, S2CStopAnimationPacket> STREAM_CODEC = PayloadHelper.simpleStreamCodec(S2CStopAnimationPacket::encode, S2CStopAnimationPacket::new);

    private final int transitionTime;
    private final String layerName;

    public S2CStopAnimationPacket(RegistryFriendlyByteBuf buf) {
        super(buf);
        this.transitionTime = buf.readInt();
        this.layerName = buf.readUtf();
    }

    public S2CStopAnimationPacket(LevelObjectCodec<?> ownerCodec, String layerName, int transitionTime) {
        super(ownerCodec);
        this.layerName = layerName;
        this.transitionTime = transitionTime;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeInt(transitionTime);
        buf.writeUtf(layerName);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
