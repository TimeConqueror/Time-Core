package ru.timeconqueror.timecore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.timeconqueror.timecore.proxy.CommonProxy;
import ru.timeconqueror.timecore.util.debug.LogHelper;

@Mod(modid = TimeCore.MODID, name = TimeCore.NAME, version = TimeCore.VERSION)
public class TimeCore {

    public static final String MODID = "timecore";
    public static final String NAME = "TimeCore";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    public static LogHelper logHelper = new LogHelper(MODID);
    @SidedProxy(clientSide = "ru.timeconqueror.timecore.proxy.ClientProxy", serverSide = "ru.timeconqueror.timecore.proxy.ServerProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static TimeCore instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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

    @Mod.EventHandler
    public void onStart(FMLServerStartingEvent event) {

    }
}
