package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.PacketRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.common.packet.animation.S2CEndAnimationMsg;
import ru.timeconqueror.timecore.common.packet.animation.S2CStartAnimationMsg;
import ru.timeconqueror.timecore.common.packet.animation.S2CSyncAnimationsMsg;

public class InternalPacketManager {
    @AutoRegistrable
    private static final PacketRegister REGISTER = new PacketRegister(TimeCore.MODID);

    private static final String PROTOCOL_STRING = "1";

    public static final SimpleChannel INSTANCE = REGISTER.createChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals)
            .regPacket(S2CSRSendSinglePieceMsg.class, new S2CSRSendSinglePieceMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CSRClearPiecesMsg.class, new S2CSRClearPiecesMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CEndAnimationMsg.class, new S2CEndAnimationMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CSyncAnimationsMsg.class, new S2CSyncAnimationsMsg.Handler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(S2CCoffeeCapabilityDataMsg.class, ClientHandler.INSTANCE, NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(C2SCoffeeCapabilityDataMsg.class, ServerHandler.INSTANCE, NetworkDirection.PLAY_TO_SERVER)
            .asChannel();

    public static <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
        InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
