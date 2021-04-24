package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.common.packet.SimplePacketHandler;

import java.io.IOException;
import java.util.Objects;

public class S2CKickPlayerFromSPPacket {
    private final ITextComponent kickReason;

    public S2CKickPlayerFromSPPacket(ITextComponent kickReason) {
        this.kickReason = kickReason;
    }

    public static class Handler extends SimplePacketHandler<S2CKickPlayerFromSPPacket> {
        @Override
        public void encode(S2CKickPlayerFromSPPacket packet, PacketBuffer buffer) throws IOException {
            buffer.writeComponent(packet.kickReason);
        }

        @Override
        public @NotNull S2CKickPlayerFromSPPacket decode(PacketBuffer buffer) throws IOException {
            return new S2CKickPlayerFromSPPacket(buffer.readComponent());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void handleOnMainThread(S2CKickPlayerFromSPPacket packet, NetworkEvent.Context ctx) {
            Minecraft mc = Minecraft.getInstance();

            getWorld(ctx).disconnect();

            Objects.requireNonNull(mc.player).connection.onDisconnect(packet.kickReason);
        }
    }
}