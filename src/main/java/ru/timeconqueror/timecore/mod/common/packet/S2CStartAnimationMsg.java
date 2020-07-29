package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

import java.util.function.Supplier;

public class S2CStartAnimationMsg extends S2CAnimationMsg {
    private final AnimationStarter.AnimationData animationData;

    public S2CStartAnimationMsg(Entity entity, String layerName, AnimationStarter.AnimationData animationData) {
        super(entity, layerName);
        this.animationData = animationData;
    }

    private S2CStartAnimationMsg(int entityId, String layerName, AnimationStarter.AnimationData animationData) {
        super(entityId, layerName);
        this.animationData = animationData;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CStartAnimationMsg> {
        @Override
        public void encodeExtra(S2CStartAnimationMsg packet, PacketBuffer buffer) {
            AnimationStarter.AnimationData.encode(packet.animationData, buffer);
        }

        @Override
        public S2CStartAnimationMsg decodeWithExtraData(int entityId, String layerName, PacketBuffer buffer) {
            AnimationStarter.AnimationData animationData = AnimationStarter.AnimationData.decode(buffer);

            return new S2CStartAnimationMsg(entityId, layerName, animationData);
        }

        @Override
        public void onPacket(S2CStartAnimationMsg packet, AnimationProvider<?> provider, String layerName, Supplier<NetworkEvent.Context> contextSupplier) {
            AnimationStarter animationStarter = AnimationStarter.fromAnimationData(packet.animationData);
            Animation animation = animationStarter.getData().getAnimation();

            String errorMessage = null;

            if (animation == null) {
                errorMessage = "Client received an animation, which is not registered on client.";
            }

            if (errorMessage == null) {
                animationStarter.startAt(provider.getActionManager().getAnimationManager(), packet.layerName);
            } else {
                TimeCore.LOGGER.error(errorMessage);
            }
        }
    }
}
