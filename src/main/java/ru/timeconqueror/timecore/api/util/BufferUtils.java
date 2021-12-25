package ru.timeconqueror.timecore.api.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;

public class BufferUtils {
    public static void encodeBoundingBox(AABB boundingBox, FriendlyByteBuf bufferTo) {
        bufferTo.writeDouble(boundingBox.minX);
        bufferTo.writeDouble(boundingBox.minY);
        bufferTo.writeDouble(boundingBox.minZ);
        bufferTo.writeDouble(boundingBox.maxX);
        bufferTo.writeDouble(boundingBox.maxY);
        bufferTo.writeDouble(boundingBox.maxZ);
    }

    public static AABB decodeBoundingBox(FriendlyByteBuf bufferFrom) {
        return new AABB(
                bufferFrom.readDouble(),
                bufferFrom.readDouble(),
                bufferFrom.readDouble(),
                bufferFrom.readDouble(),
                bufferFrom.readDouble(),
                bufferFrom.readDouble()
        );
    }
}
