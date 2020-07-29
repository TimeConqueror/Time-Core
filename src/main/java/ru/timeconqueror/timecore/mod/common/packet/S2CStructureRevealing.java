package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.Optional;
import java.util.function.Supplier;

public class S2CStructureRevealing implements ITimePacket {
    private final AxisAlignedBB bb;
    private final ResourceLocation structureName;
    private final int dimension;

    public S2CStructureRevealing(AxisAlignedBB bb, ResourceLocation structureName, int dimension) {
        this.bb = bb;
        this.structureName = structureName;
        this.dimension = dimension;
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public static class PacketHandler implements ITimePacketHandler<S2CStructureRevealing> {

        public static void encodeAABB(AxisAlignedBB boundingBox, PacketBuffer bufferTo) {
            bufferTo.writeDouble(boundingBox.minX);
            bufferTo.writeDouble(boundingBox.minY);
            bufferTo.writeDouble(boundingBox.minZ);
            bufferTo.writeDouble(boundingBox.maxX);
            bufferTo.writeDouble(boundingBox.maxY);
            bufferTo.writeDouble(boundingBox.maxZ);
        }

        public static AxisAlignedBB decodeAABB(PacketBuffer bufferFrom) {
            return new AxisAlignedBB(
                    bufferFrom.readDouble(),
                    bufferFrom.readDouble(),
                    bufferFrom.readDouble(),
                    bufferFrom.readDouble(),
                    bufferFrom.readDouble(),
                    bufferFrom.readDouble()
            );
        }

        @Override
        public void encode(S2CStructureRevealing packet, PacketBuffer buffer) {
            encodeAABB(packet.bb, buffer);
            buffer.writeResourceLocation(packet.structureName);
            buffer.writeInt(packet.dimension);
        }

        @Override
        public @NotNull S2CStructureRevealing decode(PacketBuffer buffer) {
            return new S2CStructureRevealing(decodeAABB(buffer), buffer.readResourceLocation(), buffer.readInt());
        }

        @Override
        public void onPacketReceived(S2CStructureRevealing packet, Supplier<NetworkEvent.Context> contextSupplier) {
            Optional<StructureRevealer> instance = StructureRevealer.getInstance();
            if (instance.isPresent()) {
                instance.get().structureRenderer.trackStructurePiece(packet.structureName, packet.bb, packet.dimension);
            } else {
                TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
            }
        }
    }
}
