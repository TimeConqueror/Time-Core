package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier;

import java.util.function.Supplier;

public abstract class S2CAnimationMsg implements ITimePacket {
    protected final String layerName;
    protected final CodecSupplier codecSupplier;

    protected S2CAnimationMsg(CodecSupplier codecSupplier, String layerName) {
        this.codecSupplier = codecSupplier;
        this.layerName = layerName;
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public abstract static class Handler<T extends S2CAnimationMsg> implements ITimePacketHandler<T> {
        @Override
        public final void encode(T packet, PacketBuffer buffer) {
            packet.codecSupplier.toBuffer(buffer);

            buffer.writeString(packet.layerName);

            encodeExtra(packet, buffer);
        }

        @Override
        public @NotNull
        final T decode(PacketBuffer buffer) {
            CodecSupplier codecSupplier = CodecSupplier.fromBuffer(buffer);

            String layerName = buffer.readString();
            return decodeWithExtraData(codecSupplier, layerName, buffer);
        }

        public abstract void encodeExtra(T packet, PacketBuffer buffer);

        public abstract T decodeWithExtraData(CodecSupplier codecSupplier, String layerName, PacketBuffer buffer);

        public abstract void onPacket(T packet, AnimatedObject<?> provider, String layerName, Supplier<NetworkEvent.Context> contextSupplier);

        @Override
        public void onPacketReceived(T packet, Supplier<NetworkEvent.Context> contextSupplier) {
            AnimatedObject<?> animatedObject = packet.codecSupplier.construct(packet, contextSupplier);

            onPacket(packet, animatedObject, packet.layerName, contextSupplier);
        }
    }

}
