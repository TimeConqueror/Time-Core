package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.devtools.StructureData;
import ru.timeconqueror.timecore.devtools.StructureRevealer;
import ru.timeconqueror.timecore.util.BufferUtils;

import java.util.Optional;
import java.util.function.Supplier;

public class S2CSRSendSinglePieceMsg implements ITimePacket {
    private final StructureData data;

    public S2CSRSendSinglePieceMsg(StructureData data) {
        this.data = data;
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public static class Handler implements ITimePacketHandler<S2CSRSendSinglePieceMsg> {

        @Override
        public void encode(S2CSRSendSinglePieceMsg packet, PacketBuffer buffer) {
            StructureData data = packet.data;

            BufferUtils.encodeBoundingBox(data.getBoundingBox(), buffer);
            buffer.writeResourceLocation(data.getStructureName());
            buffer.writeInt(data.getDimensionId());
        }

        @Override
        public @NotNull S2CSRSendSinglePieceMsg decode(PacketBuffer buffer) {
            StructureData data = new StructureData(BufferUtils.decodeBoundingBox(buffer), buffer.readResourceLocation(), buffer.readInt());
            return new S2CSRSendSinglePieceMsg(data);
        }

        @Override
        public void onPacketReceived(S2CSRSendSinglePieceMsg packet, Supplier<NetworkEvent.Context> contextSupplier) {
            Optional<StructureRevealer> instance = StructureRevealer.getInstance();
            if (instance.isPresent()) {
                instance.get().structureRenderer.trackStructurePiece(packet.data);
            } else {
                TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
            }
        }
    }
}
