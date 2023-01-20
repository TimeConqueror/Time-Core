package ru.timeconqueror.timecore.api.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.MarkerFilter;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.mixins.accessor.MinecraftServerAccessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnvironmentUtils {
    public static boolean isOnPhysicalClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isOnPhysicalServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static boolean isInDev() {
        return !FMLEnvironment.production;
    }

    public static boolean isInDataMode() {
        return FMLLoader.getLaunchHandler().isData();
    }

    public static Path getWorldSaveDir() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            throw new IllegalStateException("Server can't be got due to it hasn't started yet.");
        }


        return ((MinecraftServerAccessor) server).getStorageSource().getWorldDir();
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    /**
     * Enables all log messages with provided markers.
     */
    public static void enableLogMarkers(Marker... markers) {
        changeLogMarkerStates(true, markers);
    }

    /**
     * Disables all log messages with provided markers.
     */
    public static void disableLogMarkers(Marker... markers) {
        changeLogMarkerStates(false, markers);
    }

    /**
     * Enables all log messages with provided marker names.
     */
    public static void enableLogMarkers(String... markers) {
        changeLogMarkerStates(true, markers);
    }

    /**
     * Disables all log messages with provided marker names.
     */
    public static void disableLogMarkers(String... markers) {
        changeLogMarkerStates(false, markers);
    }

    private static void changeLogMarkerStates(boolean enable, Marker... markers) {
        changeLogMarkerStates(enable, CollectionUtils.mapArray(markers, String[]::new, Marker::getName));
    }

    private static void changeLogMarkerStates(boolean enable, String... markerNames) {
        if (markerNames.length == 0) return;

        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        for (String marker : markerNames) {
            context.getConfiguration().addFilter(MarkerFilter.createFilter(marker, enable ? Filter.Result.ACCEPT : Filter.Result.DENY, Filter.Result.NEUTRAL));
        }

        context.updateLoggers();
    }

    /**
     * Should be called only from the main thread!
     * Disables or enables the provided {@code availableMarkers} depending on value, which is put in
     * the system property named under {@code markerProperty}
     * <p>
     * Example of system property syntax:
     * -Dtimecore.logging.markers=RESOURCE_SYSTEM,REVEALER,REGISTRY
     *
     * @param modId            modId of your mod
     * @param markerProperty   name of system property from which values can be get
     * @param availableMarkers markers, which can be enabled. By default they will be disabled.
     */
    public static void handleMarkerVisibility(String modId, String markerProperty, Marker[] availableMarkers) {
        String markerPropValue = System.getProperty(markerProperty);
        String[] markers = markerPropValue != null ? markerPropValue.split(",") : new String[0];

        List<String> enabledMarkers = new ArrayList<>();
        String[] disabledMarkers = Arrays.stream(availableMarkers)
                .map(Marker::getName)
                .filter(name -> {
                    for (String enabled : markers) {
                        if (name.equals(enabled)) {
                            enabledMarkers.add(name);
                            return false;
                        }
                    }

                    return true;
                }).toArray(String[]::new);

        if (!enabledMarkers.isEmpty()) {
            TimeCore.LOGGER.info("Enabled logger markers for mod id {}: {}", modId, enabledMarkers);
        }
        EnvironmentUtils.disableLogMarkers(disabledMarkers);
    }
}
