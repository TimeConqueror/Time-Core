package com.timeconqueror.timecore;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
    public Logger logger;
    private boolean devEnv = false;
    private boolean debugMode = false;

    /**
     * Must be called only in {@link FMLPreInitializationEvent} event.
     */
    public LogHelper(String modid) {
        logger = LogManager.getLogger(modid);

        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            devEnv = true;
            info(TextFormatting.GREEN + "Dev enviroment was detected. Additional dev messages are enabled.");
        }
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String msg, Object... params) {
        logger.info(msg, params);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Object... params) {
        logger.warn(msg, params);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Object... params) {
        logger.error(msg, params);
    }

    public void fatal(String msg) {
        logger.fatal(msg);
    }

    public void fatal(String msg, Object... params) {
        logger.fatal(msg, params);
    }

    /**
     * Only works when {@link #debugMode} is enabled.
     */
    public void debug(String msg) {
        if (debugMode) {
            logger.debug(msg);
        }
    }

    /**
     * Only works when {@link #debugMode} is enabled.
     */
    public void debug(String msg, Object... params) {
        if (debugMode) {
            logger.debug(msg, params);
        }
    }

    /**
     * Only works when {@link #devEnv} is enabled.
     */
    public void printDevOnlyMessage(String msg) {
        if (!devEnv) {
            return;
        }

        warn(msg);
        warn("This message appears only in dev enviroment.");
    }

    public void setDebugMode(boolean state) {
        debugMode = state;
    }

    public boolean isInDev() {
        return devEnv;
    }

    public boolean isDebugModeEnabled() {
        return debugMode;
    }
}
