package ru.timeconqueror.timecore.internal.common.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.timeconqueror.timecore.TimeCore;

@Getter
@RequiredArgsConstructor
public class S2CKickPlayerFromSPPacket implements CustomPacketPayload {
    public static final Type<S2CKickPlayerFromSPPacket> TYPE = TimeCore.payloadType(S2CKickPlayerFromSPPacket.class);
    public static StreamCodec<FriendlyByteBuf, S2CKickPlayerFromSPPacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC, S2CKickPlayerFromSPPacket::getKickReason, S2CKickPlayerFromSPPacket::new);

    private final Component kickReason;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}