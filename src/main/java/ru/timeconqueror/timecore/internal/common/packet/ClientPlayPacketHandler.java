package ru.timeconqueror.timecore.internal.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.DisconnectionDetails;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.common.packet.PayloadHelper;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStartAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStopAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CSyncAnimationsPacket;

import java.util.Objects;

public class ClientPlayPacketHandler {
    public static void handleKickPlayerFromSPPacket(S2CKickPlayerFromSPPacket packet, IPayloadContext ctx) {
        Minecraft mc = Minecraft.getInstance();

        PayloadHelper.getLevel(ctx).disconnect();

        Objects.requireNonNull(mc.player).connection.onDisconnect(new DisconnectionDetails(packet.getKickReason()));
    }

    public static void handleStartAnimationPacket(S2CStartAnimationPacket packet, IPayloadContext ctx) {
        AnimatedObject<?> owner = getAnimatedObject(packet.getOwnerCodec(), ctx);

        AnimationScript animationScript = packet.getAnimationScript();
        Animation animation = animationScript.getAnimationData().getAnimation();

        if (animation == null) {
            TimeCore.LOGGER.error("Client received an animation, which is not registered on client.");
            return;
        }

        owner.animationSystem().getAnimationManager().startAnimationScript(animationScript, packet.getLayerName());
    }

    public static void handleStopAnimationPacket(S2CStopAnimationPacket packet, IPayloadContext ctx) {
        AnimatedObject<?> owner = getAnimatedObject(packet.getOwnerCodec(), ctx);

        AnimationManager animationManager = owner.animationSystem().getAnimationManager();
        animationManager.stopAnimation(packet.getLayerName(), packet.getTransitionTime());
    }

    public static void handleSyncAnimationsPacket(S2CSyncAnimationsPacket packet, IPayloadContext ctx) {
        AnimatedObject<?> owner = getAnimatedObject(packet.getOwnerCodec(), ctx);
        ((BaseAnimationManager) owner.animationSystem().getAnimationManager()).setLayersState(packet.getStatesByLayer());
    }

    private static AnimatedObject<?> getAnimatedObject(LevelObjectCodec<?> codec, IPayloadContext ctx) {
        return (AnimatedObject<?>) codec.construct(PayloadHelper.getLevel(ctx));
    }
}
