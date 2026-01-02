package ru.timeconqueror.timecore.internal.common.packet.animation;

import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationScriptImpl;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.common.packet.PayloadHelper;

@Getter
public class S2CStartAnimationPacket extends S2CAnimationPacket {
    public static final Type<S2CStartAnimationPacket> TYPE = TimeCore.payloadType(S2CStartAnimationPacket.class);
    public static StreamCodec<RegistryFriendlyByteBuf, S2CStartAnimationPacket> STREAM_CODEC = PayloadHelper.simpleStreamCodec(S2CStartAnimationPacket::encode, S2CStartAnimationPacket::new);

    private final AnimationScript animationScript;
    private final String layerName;

    public S2CStartAnimationPacket(RegistryFriendlyByteBuf buf) {
        super(buf);
        this.animationScript = AnimationScriptImpl.decode(buf);
        this.layerName = buf.readUtf();
    }

    public S2CStartAnimationPacket(LevelObjectCodec<?> ownerCodec, String layerName, AnimationScript animationScript) {
        super(ownerCodec);
        this.layerName = layerName;
        this.animationScript = animationScript;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        super.encode(buf);
        AnimationScriptImpl.encode(animationScript, buf);
        buf.writeUtf(layerName);
    }
}
