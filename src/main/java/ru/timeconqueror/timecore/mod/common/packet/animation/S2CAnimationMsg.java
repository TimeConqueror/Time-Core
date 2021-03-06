package ru.timeconqueror.timecore.mod.common.packet.animation;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.packet.SimplePacketHandler;

public abstract class S2CAnimationMsg {
    protected final String layerName;
    protected final CodecSupplier codecSupplier;

    protected S2CAnimationMsg(CodecSupplier codecSupplier, String layerName) {
        this.codecSupplier = codecSupplier;
        this.layerName = layerName;
    }

    public abstract static class Handler<T extends S2CAnimationMsg> extends SimplePacketHandler<T> {
        @Override
        public final void encode(T packet, PacketBuffer buffer) {
            packet.codecSupplier.toBuffer(buffer);

            buffer.writeUtf(packet.layerName);

            encodeExtra(packet, buffer);
        }

        @Override
        public @NotNull
        final T decode(PacketBuffer buffer) {
            CodecSupplier codecSupplier = CodecSupplier.fromBuffer(buffer);

            String layerName = buffer.readUtf();
            return decodeWithExtraData(codecSupplier, layerName, buffer);
        }

        public abstract void encodeExtra(T packet, PacketBuffer buffer);

        public abstract T decodeWithExtraData(CodecSupplier codecSupplier, String layerName, PacketBuffer buffer);

        public abstract void onPacket(T packet, AnimatedObject<?> provider, String layerName, NetworkEvent.Context ctx);

        @Override
        public void handleOnMainThread(T packet, NetworkEvent.Context ctx) {
            AnimatedObject<?> animatedObject = packet.codecSupplier.construct(getWorld(ctx));

            onPacket(packet, animatedObject, packet.layerName, ctx);
        }
    }

}
