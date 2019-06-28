package com.timeconqueror.timecore.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import example.ModEntities;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ModEntities.register();
        ModEntities.registerSpawnEggs();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
