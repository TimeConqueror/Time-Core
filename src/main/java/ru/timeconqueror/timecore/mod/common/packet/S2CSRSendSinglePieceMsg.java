package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.api.util.BufferUtils;
import ru.timeconqueror.timecore.devtools.StructureData;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.Optional;

public class S2CSRSendSinglePieceMsg implements ITimePacket {
    private final StructureData data;

    public S2CSRSendSinglePieceMsg(StructureData data) {
        this.data = data;
    }

    public static class Handler implements ITimePacketHandler<S2CSRSendSinglePieceMsg> {

        @Override
        public void encode(S2CSRSendSinglePieceMsg packet, PacketBuffer buffer) {
            StructureData data = packet.data;

            BufferUtils.encodeBoundingBox(data.getBoundingBox(), buffer);
            buffer.writeResourceLocation(data.getStructureName());
            buffer.writeResourceLocation(data.getWorldId());
        }

        @Override
        public @NotNull S2CSRSendSinglePieceMsg decode(PacketBuffer buffer) {
            StructureData data = new StructureData(BufferUtils.decodeBoundingBox(buffer), buffer.readResourceLocation(), buffer.readResourceLocation());
            return new S2CSRSendSinglePieceMsg(data);
        }

        @Override
        public boolean handle(S2CSRSendSinglePieceMsg packet, NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                Optional<StructureRevealer> instance = StructureRevealer.getInstance();
                if (instance.isPresent()) {
                    instance.get().structureRenderer.trackStructurePiece(packet.data);
                } else {
                    TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
                }
            });
            return true;
        }
    }
}
