package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.registry.PacketTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class InternalPacketManager extends PacketTimeRegistry {
    private static final String PROTOCOL_STRING = "1";
    public static final SimpleChannel INSTANCE = PacketTimeRegistry.newChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals);

    @Override
    protected void register() {
        regPacket(INSTANCE, StructureRevealingS2CPacket.class, new StructureRevealingS2CPacket.PacketHandler());
    }
}
