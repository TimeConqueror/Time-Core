package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.Animation;

public class S2CStartAnimationMsg extends S2CAnimationMsg {
    private final AnimationStarter.AnimationData animationData;
    private final String layerName;

    public S2CStartAnimationMsg(CodecSupplier codecSupplier, String layerName, AnimationStarter.AnimationData animationData) {
        super(codecSupplier);
        this.layerName = layerName;
        this.animationData = animationData;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CStartAnimationMsg> {
        @Override
        public void encodeExtra(S2CStartAnimationMsg packet, FriendlyByteBuf buffer) {
            AnimationStarter.AnimationData.encode(packet.animationData, buffer);
            buffer.writeUtf(packet.layerName);
        }

        @Override
        public S2CStartAnimationMsg decodeWithExtraData(CodecSupplier codecSupplier, FriendlyByteBuf buffer) {
            AnimationStarter.AnimationData animationData = AnimationStarter.AnimationData.decode(buffer);
            String layerName = buffer.readUtf();

            return new S2CStartAnimationMsg(codecSupplier, layerName, animationData);
        }

        @Override
        public void onPacket(S2CStartAnimationMsg packet, AnimatedObject<?> provider, NetworkEvent.Context ctx) {
            AnimationStarter animationStarter = AnimationStarter.from(packet.animationData);
            Animation animation = animationStarter.getData().getAnimation();

            String errorMessage = null;

            if (animation == null) {
                errorMessage = "Client received an animation, which is not registered on client.";//TODO add anim name
            }

            if (errorMessage == null) {
                animationStarter.startAt(provider.getActionManager().getAnimationManager(), packet.layerName);
            } else {
                TimeCore.LOGGER.error(errorMessage);
            }
        }
    }
}
