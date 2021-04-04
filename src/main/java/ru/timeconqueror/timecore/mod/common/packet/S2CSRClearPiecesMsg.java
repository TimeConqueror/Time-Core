package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.Optional;

public class S2CSRClearPiecesMsg implements ITimePacket {
    public static class Handler implements ITimePacketHandler<S2CSRClearPiecesMsg> {
        @Override
        public void encode(S2CSRClearPiecesMsg packet, PacketBuffer buffer) {

        }

        @NotNull
        @Override
        public S2CSRClearPiecesMsg decode(PacketBuffer buffer) {
            return new S2CSRClearPiecesMsg();
        }

        @Override
        public boolean handle(S2CSRClearPiecesMsg packet, NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                Optional<StructureRevealer> instance = StructureRevealer.getInstance();
                if (instance.isPresent()) {
                    instance.get().structureRenderer.getTrackedStructurePieces().clear();
                } else {
                    TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
                }
            });

            return true;
        }
    }
}
