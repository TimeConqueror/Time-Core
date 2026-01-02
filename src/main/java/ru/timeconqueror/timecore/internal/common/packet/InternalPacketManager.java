package ru.timeconqueror.timecore.internal.common.packet;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStartAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStopAnimationPacket;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CSyncAnimationsPacket;

@EventBusSubscriber
public class InternalPacketManager {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(S2CKickPlayerFromSPPacket.TYPE, S2CKickPlayerFromSPPacket.STREAM_CODEC, ClientPlayPacketHandler::handleKickPlayerFromSPPacket);
        registrar.playToClient(S2CStartAnimationPacket.TYPE, S2CStartAnimationPacket.STREAM_CODEC, ClientPlayPacketHandler::handleStartAnimationPacket);
        registrar.playToClient(S2CStopAnimationPacket.TYPE, S2CStopAnimationPacket.STREAM_CODEC, ClientPlayPacketHandler::handleStopAnimationPacket);
        registrar.playToClient(S2CSyncAnimationsPacket.TYPE, S2CSyncAnimationsPacket.STREAM_CODEC, ClientPlayPacketHandler::handleSyncAnimationsPacket);
    }
}
