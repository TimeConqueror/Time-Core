package ru.timeconqueror.timecore.api.common.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.registry.PacketRegister;

/**
 * Packet interface for using with {@link PacketRegister#regPacket(SimpleChannel, Class)}
 * Does not require extra handler class, since all logic is done inside {@link IPacket} class itself.
 * <br>
 * Note: Deserialization is done without calling packet constructor.
 *
 * @see PacketRegister#regPacket(SimpleChannel, Class)
 */
public interface IPacket {
    void write(FriendlyByteBuf buf);

    void read(FriendlyByteBuf buf);

    default void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            handleOnClient(ctx);
        } else {
            handleOnServer(ctx);
        }
    }

    default void handleOnClient(NetworkEvent.Context ctx) {
    }

    default void handleOnServer(NetworkEvent.Context ctx) {
    }
}
