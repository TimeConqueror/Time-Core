package ru.timeconqueror.timecore.mod.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

import java.util.function.Supplier;

public class StructureRevealingS2CPacket implements ITimePacket {
    private final AxisAlignedBB bb;
    private final ResourceLocation structureName;

    public StructureRevealingS2CPacket(AxisAlignedBB bb, ResourceLocation structureName) {
        this.bb = bb;
        this.structureName = structureName;
    }

    @Override
    public LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public static class PacketHandler implements ITimePacketHandler<StructureRevealingS2CPacket> {

        @Override
        public void encode(StructureRevealingS2CPacket packet, PacketBuffer buffer) {
            encodeAABB(packet.bb, buffer);
            buffer.writeResourceLocation(packet.structureName);
        }

        @Override
        public @NotNull StructureRevealingS2CPacket decode(PacketBuffer buffer) {
            return new StructureRevealingS2CPacket(decodeAABB(buffer), buffer.readResourceLocation());
        }

        @Override
        public void onPacketReceived(StructureRevealingS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            StructureRevealer instance = StructureRevealer.getInstance();
            if (instance != null && instance.structureRenderer != null) {
                instance.structureRenderer.trackStructurePiece(packet.structureName, packet.bb);
            }
        }

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
    }
}
