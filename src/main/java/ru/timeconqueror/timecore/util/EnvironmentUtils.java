package ru.timeconqueror.timecore.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class EnvironmentUtils {
    /**
     * Will be changed to true via reflection, if Minecraft is run in Data Mode.
     */
    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private static boolean isInDataMode = false;

    public static boolean isOnPhysicalClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isOnPhysicalServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static boolean isInDev() {//TODO maybe move to FMLEnvironment.production
        return !FMLEnvironment.production;
    }

    public static boolean isInDataMode() {
        return isInDataMode;
    }

    public static Path getWorldSaveDir() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            throw new IllegalStateException("Server can't be got due to it hasn't started yet.");
        }

        return server.getActiveAnvilConverter().getFile(server.getFolderName(), "").toPath();
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
