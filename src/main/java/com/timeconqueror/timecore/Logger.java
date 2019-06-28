package com.timeconqueror.timecore;

public class Logger {
    public static org.apache.logging.log4j.Logger logger = TimeCore.getLogger();

    public static void info(String msg){
        logger.info(msg);
    }

    public static void warn(String msg){
        logger.warn(msg);
    }

    public static void warn(String msg, Object... params){
        logger.warn(msg, params);
    }

    public static void error(String msg){
        logger.error(msg);
    }

    public static void error(String msg, Object... params){
        logger.error(msg, params);
    }

    public static void fatal(String msg){
        logger.fatal(msg);
    }

    public static void printDevOnlyMessage(String msg){
        Logger.warn(msg);
        Logger.warn("This message appears only in dev enviroment.");
    }
}
