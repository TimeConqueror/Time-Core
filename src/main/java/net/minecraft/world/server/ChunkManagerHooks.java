package net.minecraft.world.server;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

public class ChunkManagerHooks {
    public static Iterable<ChunkHolder> getLoadedChunksIterable(ChunkMap chunkManager) {
        return chunkManager.getChunks();
    }
}
