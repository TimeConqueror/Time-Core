package ru.timeconqueror.timecore.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class EnvironmentUtils {
    public static boolean isOnPhysicalClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isOnPhysicalServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static boolean isInDev() {
        return FMLEnvironment.naming.equals("mcp");
    }

    public static boolean isInDataMode() {
        return true;//FIXME
    }

    public static void runIfInDataMode(Runnable runnable) {//FIXME
        runnable.run();
    }

    public static Path getWorldSaveDir() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            throw new IllegalStateException("Server can't be got due to it hasn't started yet.");
        }

        return server.getActiveAnvilConverter().getFile(server.getFolderName(), "").toPath();
    }
}
