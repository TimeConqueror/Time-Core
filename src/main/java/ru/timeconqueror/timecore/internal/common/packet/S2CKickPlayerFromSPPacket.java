package ru.timeconqueror.timecore.internal.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;

import java.io.IOException;
import java.util.Objects;

public class S2CKickPlayerFromSPPacket {
    private final Component kickReason;

    public S2CKickPlayerFromSPPacket(Component kickReason) {
        this.kickReason = kickReason;
    }

    public static class Handler implements ITimePacketHandler<S2CKickPlayerFromSPPacket> {
        @Override
        public void encode(S2CKickPlayerFromSPPacket packet, FriendlyByteBuf buffer) throws IOException {
            buffer.writeComponent(packet.kickReason);
        }

        @Override
        public @NotNull S2CKickPlayerFromSPPacket decode(FriendlyByteBuf buffer) throws IOException {
            return new S2CKickPlayerFromSPPacket(buffer.readComponent());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void handle(S2CKickPlayerFromSPPacket packet, NetworkEvent.Context ctx) {
            Minecraft mc = Minecraft.getInstance();

            getWorld(ctx).disconnect();

            Objects.requireNonNull(mc.player).connection.onDisconnect(packet.kickReason);
        }
    }
}