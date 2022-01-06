package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;

public class S2CEndAnimationMsg extends S2CAnimationMsg {
    private final int transitionTime;

    public S2CEndAnimationMsg(CodecSupplier codecSupplier, String layerName, int transitionTime) {
        super(codecSupplier, layerName);
        this.transitionTime = transitionTime;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CEndAnimationMsg> {

        @Override
        public void onPacket(S2CEndAnimationMsg packet, AnimatedObject<?> provider, String layerName, NetworkEvent.Context ctx) {
            AnimationManager animationManager = provider.getActionManager().getAnimationManager();
            animationManager.removeAnimation(layerName, packet.transitionTime);
        }

        @Override
        public void encodeExtra(S2CEndAnimationMsg packet, FriendlyByteBuf buffer) {
            buffer.writeInt(packet.transitionTime);
        }

        @Override
        public S2CEndAnimationMsg decodeWithExtraData(CodecSupplier codecSupplier, String layerName, FriendlyByteBuf buffer) {
            int transitionTime = buffer.readInt();
            return new S2CEndAnimationMsg(codecSupplier, layerName, transitionTime);
        }
    }
}
