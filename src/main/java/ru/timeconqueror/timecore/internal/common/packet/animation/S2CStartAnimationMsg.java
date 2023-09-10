package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.AnimationData;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.Animation;

public class S2CStartAnimationMsg extends S2CAnimationMsg {
    private final AnimationData animationData;
    private final String layerName;

    public S2CStartAnimationMsg(LevelObjectCodec<?> ownerCodec, String layerName, AnimationData animationData) {
        super(ownerCodec);
        this.layerName = layerName;
        this.animationData = animationData;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CStartAnimationMsg> {
        @Override
        public void encodeExtra(S2CStartAnimationMsg packet, FriendlyByteBuf buffer) {
            AnimationData.encode(packet.animationData, buffer);
            buffer.writeUtf(packet.layerName);
        }

        @Override
        public S2CStartAnimationMsg decodeWithExtraData(LevelObjectCodec<?> codecSupplier, FriendlyByteBuf buffer) {
            AnimationData animationData = AnimationData.decode(buffer);
            String layerName = buffer.readUtf();

            return new S2CStartAnimationMsg(codecSupplier, layerName, animationData);
        }

        @Override
        public void onPacket(S2CStartAnimationMsg packet, AnimatedObject<?> owner, NetworkEvent.Context ctx) {
            var data = packet.animationData;

            Animation animation = data.getAnimation();

            if (animation == null) {
                TimeCore.LOGGER.error("Client received an animation, which is not registered on client.");
                return;
            }

            owner.getSystem().getAnimationManager().startAnimation(data, packet.layerName, AnimationCompanionData.EMPTY);
        }
    }
}
