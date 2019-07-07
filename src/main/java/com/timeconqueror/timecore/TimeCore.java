package com.timeconqueror.timecore;

import com.timeconqueror.timecore.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TimeCore.MODID, name = TimeCore.NAME, version = TimeCore.VERSION)
public class TimeCore {

    public static final String MODID = "timecore";
    public static final String NAME = "Time Core";
    public static final String VERSION = "1.0";

    public static LogHelper logHelper = new LogHelper(MODID);

    @SidedProxy(clientSide = "com.timeconqueror.timecore.proxy.ClientProxy", serverSide = "com.timeconqueror.timecore.proxy.ServerProxy")
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
}
