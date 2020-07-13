package ru.timeconqueror.timecore.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class EnvironmentUtils {
    public static boolean isOnClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isOnServer() {
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
}
