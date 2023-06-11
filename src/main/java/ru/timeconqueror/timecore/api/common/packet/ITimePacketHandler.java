package ru.timeconqueror.timecore.api.common.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
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
    void encode(T packet, FriendlyByteBuf buffer) throws IOException;

    /**
     * Decodes packet from provided buffer.
     * <p>
     * Here you should read data from buffer and then create and return baked packet object.
     *
     * @param buffer buffer, from which data will be read
     */
    @NotNull
    T decode(FriendlyByteBuf buffer) throws IOException;

    /**
     * Handles received packet.
     * Called from the <b>main</b> thread.
     */
    void handle(T packet, NetworkEvent.Context ctx);

    @NotNull
    @SuppressWarnings("ConstantConditions")
    default Level getWorld(NetworkEvent.Context ctx) {
        return ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT ? ClientProxy.world() : ctx.getSender().level();
    }
}
