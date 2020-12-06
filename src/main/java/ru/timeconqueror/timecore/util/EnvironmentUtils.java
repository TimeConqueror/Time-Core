package ru.timeconqueror.timecore.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MinecraftServerHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.MarkerFilter;

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

        return MinecraftServerHooks.getStorageSource(server).getWorldDir();
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    /**
     * Enables all log messages with provided markers.
     */
    public static void enableLogMarkers(Marker... markers) {
        changeLogMarkerState(true, markers);
    }

    /**
     * Disables all log messages with provided markers.
     */
    public static void disableLogMarker(Marker... markers) {
        changeLogMarkerState(false, markers);
    }

    /**
     * Enables all log messages with provided marker names.
     */
    public static void enableLogMarker(String... markers) {
        changeLogMarkerState(true, markers);
    }

    /**
     * Disables all log messages with provided marker names.
     */
    public static void disableLogMarker(String... markers) {
        changeLogMarkerState(false, markers);
    }

    private static void changeLogMarkerState(boolean enable, Marker... markers) {
        changeLogMarkerState(enable, CollectionUtils.map(markers, String[]::new, Marker::getName));
    }

    private static void changeLogMarkerState(boolean enable, String... markerNames) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        for (String marker : markerNames) {
            context.getConfiguration().addFilter(MarkerFilter.createFilter(marker, enable ? Filter.Result.ACCEPT : Filter.Result.DENY, Filter.Result.NEUTRAL));
        }

        context.updateLoggers();
    }
}
