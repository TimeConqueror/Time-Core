package ru.timeconqueror.timecore.api.common.packet;

import net.minecraftforge.network.NetworkEvent;

/**
 * Packet Handler with the method {@link #handleOnMainThread(Object, NetworkEvent.Context)}, which is already redirected to be run in the main thread.
 */
public abstract class SimplePacketHandler<T> implements ITimePacketHandler<T> {
    @Override
    public boolean handle(T packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> handleOnMainThread(packet, ctx));
        return true;
    }

    public abstract void handleOnMainThread(T packet, NetworkEvent.Context ctx);
}
