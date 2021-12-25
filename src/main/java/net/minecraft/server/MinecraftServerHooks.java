package net.minecraft.server;

import net.minecraft.world.level.storage.LevelStorageSource;

public class MinecraftServerHooks {
    public static LevelStorageSource.LevelStorageAccess getStorageSource(MinecraftServer server) {
        return server.storageSource;
    }
}
