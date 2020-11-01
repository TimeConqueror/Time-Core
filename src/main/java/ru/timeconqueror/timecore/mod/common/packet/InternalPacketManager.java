package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.PacketRegister;


public class InternalPacketManager {
    private static final String PROTOCOL_STRING = "1";
    public static final SimpleChannel INSTANCE = PacketRegister.newChannel(TimeCore.MODID, "main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals);

    @AutoRegistrable
    private static final PacketRegister REGISTER = new PacketRegister(TimeCore.MODID);

    @AutoRegistrable.InitMethod
    private static void register() {
        REGISTER.regPacket(INSTANCE, S2CSRSendSinglePieceMsg.class, new S2CSRSendSinglePieceMsg.Handler());
        REGISTER.regPacket(INSTANCE, S2CSRClearPiecesMsg.class, new S2CSRClearPiecesMsg.Handler());
        REGISTER.regPacket(INSTANCE, S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler());
        REGISTER.regPacket(INSTANCE, S2CEndAnimationMsg.class, new S2CEndAnimationMsg.Handler());
        REGISTER.regPacket(INSTANCE, S2CSyncAnimationsMsg.class, new S2CSyncAnimationsMsg.Handler());
    }

    public static <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
        InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
