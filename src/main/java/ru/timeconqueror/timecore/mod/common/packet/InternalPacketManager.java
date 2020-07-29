package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.common.PacketTimeRegistry;

@TimeAutoRegistrable
public class InternalPacketManager extends PacketTimeRegistry {
    private static final String PROTOCOL_STRING = "1";
    public static final SimpleChannel INSTANCE = PacketTimeRegistry.newChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals);

    @Override
    protected void register() {
        regPacket(INSTANCE, S2CSRSendSinglePieceMsg.class, new S2CSRSendSinglePieceMsg.Handler());
        regPacket(INSTANCE, S2CSRClearPiecesMsg.class, new S2CSRClearPiecesMsg.Handler());
        regPacket(INSTANCE, S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler());
        regPacket(INSTANCE, S2CEndAnimationMsg.class, new S2CEndAnimationMsg.Handler());
        regPacket(INSTANCE, S2CSyncAnimationsMsg.class, new S2CSyncAnimationsMsg.Handler());
    }

    public static <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
        InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
