package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier;

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
        public void onPacketReceived(T packet, NetworkEvent.Context ctx, World world) {
            AnimatedObject<?> animatedObject = packet.codecSupplier.construct(world);

            onPacket(packet, animatedObject, packet.layerName, ctx);
        }
    }

}
