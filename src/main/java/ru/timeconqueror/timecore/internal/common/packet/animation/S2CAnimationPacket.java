package ru.timeconqueror.timecore.internal.common.packet.animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;

@Getter
@AllArgsConstructor
public abstract class S2CAnimationPacket implements CustomPacketPayload {

    protected final LevelObjectCodec<?> ownerCodec;

    public S2CAnimationPacket(RegistryFriendlyByteBuf buf) {
        ownerCodec = LevelObjectCodec.STREAM_CODEC.decode(buf);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        LevelObjectCodec.STREAM_CODEC.encode(buf, ownerCodec);
    }
}
