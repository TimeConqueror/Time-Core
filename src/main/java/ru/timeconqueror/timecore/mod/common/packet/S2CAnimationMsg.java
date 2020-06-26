package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
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

    public S2CAnimationMsg(int entityId, String layerName) {
        this.entityId = entityId;
        this.layerName = layerName;
    }

    @Override
    public LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    protected static class Data {
        protected int entityId;
        protected String layerName;

        public Data(S2CAnimationMsg msg) {
            this(msg.entityId, msg.layerName);
        }

        public Data(int entityId, String layerName) {
            this.entityId = entityId;
            this.layerName = layerName;
        }
    }

    public abstract static class Handler<T extends S2CAnimationMsg> implements ITimePacketHandler<T> {
        public void encodeBaseData(S2CAnimationMsg msg, PacketBuffer buffer) {
            Data data = new Data(msg);

            buffer.writeInt(data.entityId);
            buffer.writeString(data.layerName);
        }

        public Data decodeBaseData(PacketBuffer buffer) {
            return new Data(buffer.readInt(), buffer.readString());
        }

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
