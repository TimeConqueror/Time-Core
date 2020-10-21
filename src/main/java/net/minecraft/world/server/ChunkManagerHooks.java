package net.minecraft.world.server;

public class ChunkManagerHooks {
	public static Iterable<ChunkHolder> getLoadedChunksIterable(ChunkManager chunkManager) {
		return chunkManager.getChunks();
	}
}
