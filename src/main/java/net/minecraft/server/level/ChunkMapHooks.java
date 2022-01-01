package net.minecraft.server.level;

public class ChunkMapHooks {
    public static Iterable<ChunkHolder> getLoadedChunksIterable(ChunkMap chunkManager) {
        return chunkManager.getChunks();
    }
}
