package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.packet.SimplePacketHandler;
import ru.timeconqueror.timecore.api.util.BufferUtils;
import ru.timeconqueror.timecore.devtools.StructureData;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.Optional;

public class S2CSRSendSinglePiecePacket {
    private final StructureData data;

    public S2CSRSendSinglePiecePacket(StructureData data) {
        this.data = data;
    }

    public static class Handler extends SimplePacketHandler<S2CSRSendSinglePiecePacket> {

        @Override
        public void encode(S2CSRSendSinglePiecePacket packet, PacketBuffer buffer) {
            StructureData data = packet.data;

            BufferUtils.encodeBoundingBox(data.getBoundingBox(), buffer);
            buffer.writeResourceLocation(data.getStructureName());
            buffer.writeResourceLocation(data.getWorldId());
        }

        @Override
        public @NotNull S2CSRSendSinglePiecePacket decode(PacketBuffer buffer) {
            StructureData data = new StructureData(BufferUtils.decodeBoundingBox(buffer), buffer.readResourceLocation(), buffer.readResourceLocation());
            return new S2CSRSendSinglePiecePacket(data);
        }

        @Override
        public void handleOnMainThread(S2CSRSendSinglePiecePacket packet, NetworkEvent.Context ctx) {
            Optional<StructureRevealer> instance = StructureRevealer.getInstance();
            if (instance.isPresent()) {
                instance.get().structureRenderer.trackStructurePiece(packet.data);
            } else {
                TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
            }
        }
    }
}
