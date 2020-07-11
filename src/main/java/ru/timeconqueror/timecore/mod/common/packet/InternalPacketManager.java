package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.common.PacketTimeRegistry;

@TimeAutoRegistrable
public class InternalPacketManager extends PacketTimeRegistry {
    private static final String PROTOCOL_STRING = "1";
    public static final SimpleChannel INSTANCE = PacketTimeRegistry.newChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals);

    @Override
    protected void register() {
        regPacket(INSTANCE, StructureRevealingS2CPacket.class, new StructureRevealingS2CPacket.PacketHandler());
        regPacket(INSTANCE, S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler());
        regPacket(INSTANCE, S2CEndAnimationMsg.class, new S2CEndAnimationMsg.Handler());
    }
}
