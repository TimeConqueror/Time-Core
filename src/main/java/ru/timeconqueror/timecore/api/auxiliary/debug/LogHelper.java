package ru.timeconqueror.timecore.api.auxiliary.debug;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.TimeCore;

public class LogHelper {
    private static final String CONSOLE_DEBUG_PREFIX = TextFormatting.AQUA + "[DEBUG] " + TextFormatting.RESET;
    private static final String CONSOLE_TRACE_PREFIX = TextFormatting.GOLD + "[TRACE] " + TextFormatting.RESET;

    public Logger logger;
    private boolean devEnv = false;
    private boolean enableDebug = false;
    private Level debugLevel = Level.DEBUG;
    /**
     * If this is equal true, then Debug messages will be printed to console, not to debug.log
     */
    private boolean debugToConsole = false;

    /**
     * Must be called only in {@link FMLPreInitializationEvent} event or during first access to the mod class.
     */
    public LogHelper(String modid) {
        logger = LogManager.getLogger(modid);

        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            devEnv = true;
            if (logger.getName().equals(TimeCore.MODID)) {
                info(TextFormatting.GREEN + "Dev enviroment was detected. Additional dev messages are enabled.");
            }
        }
    }

    public void info(String msg) {
        info(msg, new Object[0]);
    }

    public void info(String msg, Object... params) {
        logger.info(msg, params);
    }

    public void warn(String msg) {
        warn(msg, new Object[0]);
    }

    public void warn(String msg, Object... params) {
        logger.warn(msg, params);
    }

    public void error(String msg) {
        error(msg, new Object[0]);
    }

    public void error(String msg, Object... params) {
        logger.error(msg, params);
    }

    public void error(Throwable e) {
        error("", e);
    }

    public void error(String msg, Throwable e) {
        logger.error(msg, e);
    }

    public void fatal(String msg) {
        fatal(msg, new Object[0]);
    }

    public void fatal(String msg, Object... params) {
        logger.fatal(msg, params);
    }

    /**
     * Only works when {@link #enableDebug} is enabled.
     * If {@link #debugToConsole} equals true, then it will print to console and not to log file.
     */
    public void debug(String msg) {
        debug(msg, new Object[0]);
    }

    /**
     * Only works when {@link #enableDebug} is enabled.
     * If {@link #debugToConsole} equals true, then it will print to console and not to log file.
     */
    public void debug(String msg, Object... params) {
        if (enableDebug && (debugLevel == Level.DEBUG || debugLevel == Level.TRACE)) {
            if (debugToConsole) {
                logger.info(CONSOLE_DEBUG_PREFIX + msg, params);
            } else {
                logger.debug(msg, params);
            }
        }
    }

    /**
     * Only works when {@link #enableDebug} is enabled and {@link Level} is {@link Level#TRACE}.
     * If {@link #debugToConsole} equals true, then it will print to console and not to log file.
     */
    public void trace(String msg) {
        trace(msg, new Object[0]);
    }

    /**
     * Only works when {@link #enableDebug} is enabled and {@link Level} is {@link Level#TRACE}.
     * If {@link #debugToConsole} equals true, then it will print to console and not to log file.
     */
    public void trace(String msg, Object... params) {
        if (enableDebug && debugLevel == Level.TRACE) {
            if (debugToConsole) {
                logger.info(CONSOLE_TRACE_PREFIX + msg, params);
            } else {
                logger.debug(msg, params);
            }
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

    /**
     * If this is equal true, then Debug messages will be printed to console, not to debug.log
     */
    public void setPrintDebugToConsole(boolean state) {
        debugToConsole = state;
    }

    /**
     * Returns true, if messages are printing to console and not to debug.log
     */
    public boolean isDebugToConsoleOn() {
        return debugToConsole;
    }

    /**
     * Returns true, if debug messages are enabled.
     */
    public boolean isDebugEnabled() {
        return enableDebug;
    }

    /**
     * Enables debug messages.
     */
    public void setDebugEnabled(boolean state) {
        enableDebug = state;
    }

    /**
     * Sets the debug level.
     *
     * @param debugLevel <p>{@link Level#DEBUG}: prints debug messages.
     *                   <p>{@link Level#TRACE}: prints debug and trace messages.
     */
    public void setDebugLevel(Level debugLevel) {
        this.debugLevel = debugLevel;
    }

    /**
     * Returns true, if minecraft is runned in development workspace.
     */
    public boolean isInDev() {
        return devEnv;
    }

    public enum Level {
        DEBUG, TRACE
    }
}
