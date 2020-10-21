package net.minecraft.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveFormat;

public class MinecraftServerHooks {
    public static SaveFormat.LevelSave getStorageSource(MinecraftServer server){
        return server.storageSource;
    }
}
