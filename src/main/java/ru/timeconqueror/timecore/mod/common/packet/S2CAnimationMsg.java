package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;

import java.util.function.Supplier;

public abstract class S2CAnimationMsg implements ITimePacket {
    protected final int entityId;
    protected final String layerName;

    public S2CAnimationMsg(Entity entity, String layerName) {
        this(entity.getEntityId(), layerName);
    }

    protected S2CAnimationMsg(int entityId, String layerName) {
        this.entityId = entityId;
        this.layerName = layerName;
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public abstract static class Handler<T extends S2CAnimationMsg> implements ITimePacketHandler<T> {
        @Override
        public final void encode(T packet, PacketBuffer buffer) {
            buffer.writeInt(packet.entityId);
            buffer.writeString(packet.layerName);

            encodeExtra(packet, buffer);
        }

        @Override
        public @NotNull
        final T decode(PacketBuffer buffer) {
            int entityId = buffer.readInt();
            String layerName = buffer.readString();
            return decodeWithExtraData(entityId, layerName, buffer);
        }

        public abstract void encodeExtra(T packet, PacketBuffer buffer);

        public abstract T decodeWithExtraData(int entityId, String layerName, PacketBuffer buffer);

        public abstract void onPacket(T packet, AnimationProvider<?> provider, String layerName, Supplier<NetworkEvent.Context> contextSupplier);

        @Override
        public void onPacketReceived(T packet, Supplier<NetworkEvent.Context> contextSupplier) {
            String errorMessage = null;

            Entity entity = packet.getWorld(contextSupplier.get()).getEntityByID(packet.entityId);
            if (entity == null) {
                errorMessage = "Client received an animation, but entity wasn't found on client.";
            } else if (!(entity instanceof AnimationProvider<?>)) {
                errorMessage = "Provided entity id belongs to entity, which is not an inheritor of " + AnimationProvider.class;
            }

            if (errorMessage == null) {
                onPacket(packet, ((AnimationProvider<?>) entity), packet.layerName, contextSupplier);
            } else {
                TimeCore.LOGGER.error(errorMessage);
            }
        }
    }
}
