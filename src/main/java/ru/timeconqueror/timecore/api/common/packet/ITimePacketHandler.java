package ru.timeconqueror.timecore.api.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.client.ClientProxy;

import java.io.IOException;

public interface ITimePacketHandler<T> {
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

    /**
     * Handles received packet.
     * Called from the network thread, so you should be careful about enqueueing stuff to the main thread using {@link NetworkEvent.Context#enqueueWork(Runnable)}.
     *
     * @return true if handled successfully. If the method returns false, client may or may not be disconnected from server
     */
    boolean handle(T packet, NetworkEvent.Context ctx);

    @NotNull
    @SuppressWarnings("ConstantConditions")
    default World getWorld(NetworkEvent.Context ctx) {
        return ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT ? ClientProxy.world() : ctx.getSender().level;
    }
}
