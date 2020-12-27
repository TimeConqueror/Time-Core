package ru.timeconqueror.timecore.common.packet.animation;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.Animation;

public class S2CStartAnimationMsg extends S2CAnimationMsg {
    private final AnimationStarter.AnimationData animationData;

    public S2CStartAnimationMsg(CodecSupplier codecSupplier, String layerName, AnimationStarter.AnimationData animationData) {
        super(codecSupplier, layerName);
        this.animationData = animationData;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CStartAnimationMsg> {
        @Override
        public void encodeExtra(S2CStartAnimationMsg packet, PacketBuffer buffer) {
            AnimationStarter.AnimationData.encode(packet.animationData, buffer);
        }

        @Override
        public S2CStartAnimationMsg decodeWithExtraData(CodecSupplier codecSupplier, String layerName, PacketBuffer buffer) {
            AnimationStarter.AnimationData animationData = AnimationStarter.AnimationData.decode(buffer);

            return new S2CStartAnimationMsg(codecSupplier, layerName, animationData);
        }

        @Override
        public void onPacket(S2CStartAnimationMsg packet, AnimatedObject<?> provider, String layerName, NetworkEvent.Context ctx) {
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
