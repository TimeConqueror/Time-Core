package ru.timeconqueror.timecore.api.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.util.Hacks;

import java.io.IOException;
import java.util.function.Supplier;

public interface ITimePacket {
    /**
     * Should return the side, where packet can be read.
     */
    @NotNull
    LogicalSide getReceptionSide();

    interface ITimePacketHandler<T extends ITimePacket> {
        /**
         * Encodes provided {@code packet} to {@code buffer}.
         *
         * @param packet packet to encode
         * @param buffer buffer, where you should encode packet.
         */
        void encode(T packet, PacketBuffer buffer) throws IOException;

        /**
         * Decodes packet from provided buffer.
         * <p>
         * Here you should read data from buffer and then create and return baked packet object.
         *
         * @param buffer buffer, from which data will be read
         */
        @NotNull
        T decode(PacketBuffer buffer) throws IOException;

        void onPacketReceived(T packet, NetworkEvent.Context ctx, World world);

        /**
         * Handles received packet.
         * If packet was sent from wrong side, it MUSTN'T be handled due to possible exploits. This method solves this problem.
         *
         * @return true if packet is handled (came from right side) or false if not
         */
        default boolean handle(T packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context ctx = contextSupplier.get();
            if (ctx.getDirection().getReceptionSide() == packet.getReceptionSide()) {
                onPacketReceived(packet, ctx, getWorld(ctx));
                return true;
            } else return false;
        }

        @NotNull//TODO move to handler
        default World getWorld(NetworkEvent.Context ctx) {
            return FMLEnvironment.dist == Dist.CLIENT ? Hacks.bypassClassChecking(Minecraft.getInstance().level) : ctx.getSender().level;
        }
    }
}
