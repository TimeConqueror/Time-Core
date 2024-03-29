package ru.timeconqueror.timecore.internal.common.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.PacketRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStartAnimationMsg;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CStopAnimationMsg;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CSyncAnimationsMsg;

public class InternalPacketManager {
    @AutoRegistrable
    private static final PacketRegister REGISTER = new PacketRegister(TimeCore.MODID);

    private static final String PROTOCOL_STRING = "1";

    public static final SimpleChannel INSTANCE = REGISTER.createChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals)
            //  .regPacket(S2CSRSendSinglePiecePacket.class, new S2CSRSendSinglePiecePacket.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            //  .regPacket(S2CSRClearPiecesPacket.class, new S2CSRClearPiecesPacket.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            //todo port?
            .regPacket(S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CStopAnimationMsg.class, new S2CStopAnimationMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CSyncAnimationsMsg.class, new S2CSyncAnimationsMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CCoffeeCapabilityDataPacket.class, CoffeeCapabilityDataPacket.Handler.ClientHandler.INSTANCE, NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(C2SCoffeeCapabilityDataPacket.class, CoffeeCapabilityDataPacket.Handler.ServerHandler.INSTANCE, NetworkDirection.PLAY_TO_SERVER)
            .regPacket(S2CKickPlayerFromSPPacket.class, new S2CKickPlayerFromSPPacket.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .asChannel();

    public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
        InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
