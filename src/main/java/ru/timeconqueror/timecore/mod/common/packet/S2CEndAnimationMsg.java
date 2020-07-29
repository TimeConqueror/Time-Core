package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

import java.util.function.Supplier;

public class S2CEndAnimationMsg extends S2CAnimationMsg {
    private final int transitionTime;

    public S2CEndAnimationMsg(Entity entity, String layerName, int transitionTime) {
        super(entity, layerName);
        this.transitionTime = transitionTime;
    }

    public S2CEndAnimationMsg(int entityId, String layerName, int transitionTime) {
        super(entityId, layerName);
        this.transitionTime = transitionTime;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CEndAnimationMsg> {

        @Override
        public void onPacket(S2CEndAnimationMsg packet, AnimationProvider<?> provider, String layerName, Supplier<NetworkEvent.Context> contextSupplier) {
            AnimationManager animationManager = provider.getActionManager().getAnimationManager();
            animationManager.removeAnimation(layerName, packet.transitionTime);
        }

        @Override
        public void encodeExtra(S2CEndAnimationMsg packet, PacketBuffer buffer) {
            buffer.writeInt(packet.transitionTime);
        }

        @Override
        public S2CEndAnimationMsg decodeWithExtraData(int entityId, String layerName, PacketBuffer buffer) {
            int transitionTime = buffer.readInt();

            return new S2CEndAnimationMsg(entityId, layerName, transitionTime);
        }
    }
}
