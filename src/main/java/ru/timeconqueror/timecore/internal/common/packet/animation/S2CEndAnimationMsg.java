package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;

public class S2CEndAnimationMsg extends S2CAnimationMsg {
    private final int transitionTime;
    private final String layerName;

    public S2CEndAnimationMsg(CodecSupplier codecSupplier, String layerName, int transitionTime) {
        super(codecSupplier);
        this.layerName = layerName;
        this.transitionTime = transitionTime;
    }

    public static class Handler extends S2CAnimationMsg.Handler<S2CEndAnimationMsg> {

        @Override
        public void onPacket(S2CEndAnimationMsg packet, AnimatedObject<?> provider, NetworkEvent.Context ctx) {
            AnimationManager animationManager = provider.getActionManager().getAnimationManager();
            animationManager.removeAnimation(packet.layerName, packet.transitionTime);
        }

        @Override
        public void encodeExtra(S2CEndAnimationMsg packet, FriendlyByteBuf buffer) {
            buffer.writeInt(packet.transitionTime);
            buffer.writeUtf(packet.layerName);
        }

        @Override
        public S2CEndAnimationMsg decodeWithExtraData(CodecSupplier codecSupplier, FriendlyByteBuf buffer) {
            int transitionTime = buffer.readInt();
            String layerName = buffer.readUtf();
            return new S2CEndAnimationMsg(codecSupplier, layerName, transitionTime);
        }
    }
}
