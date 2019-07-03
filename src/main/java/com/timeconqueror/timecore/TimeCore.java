package com.timeconqueror.timecore;

import com.timeconqueror.timecore.proxy.CommonProxy;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TimeCore.MODID, name = TimeCore.NAME, version = TimeCore.VERSION)
public class TimeCore {
    public static boolean devEnv = false;

    public static final String MODID = "timecore";
    public static final String NAME = "Time Core";
    public static final String VERSION = "1.0";

    private static Logger logger = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.timeconqueror.timecore.proxy.ClientProxy", serverSide = "com.timeconqueror.timecore.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static TimeCore instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")){
            devEnv = true;
            logger.info(TextFormatting.GREEN + "Dev enviroment was detected. Additional debug messages are launched.");
        }
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static Logger getLogger() {
        return logger;
    }
}
