package ru.timeconqueror.timecore.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;

public class BufferUtils {
    public static void encodeBoundingBox(AxisAlignedBB boundingBox, PacketBuffer bufferTo) {
        bufferTo.writeDouble(boundingBox.minX);
        bufferTo.writeDouble(boundingBox.minY);
        bufferTo.writeDouble(boundingBox.minZ);
        bufferTo.writeDouble(boundingBox.maxX);
        bufferTo.writeDouble(boundingBox.maxY);
        bufferTo.writeDouble(boundingBox.maxZ);
    }

    public static AxisAlignedBB decodeBoundingBox(PacketBuffer bufferFrom) {
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
