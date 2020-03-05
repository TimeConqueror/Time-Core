package ru.timeconqueror.timecore.api.registry.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface ITimePacketHandler<T> {
    void encode(T packet, PacketBuffer buffer);

    T decode(PacketBuffer buffer);

    void handle(T packet, Supplier<NetworkEvent.Context> contextSupplier);
}
