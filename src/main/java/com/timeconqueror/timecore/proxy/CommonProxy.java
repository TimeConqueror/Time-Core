package com.timeconqueror.timecore.proxy;

import example.ModEntities;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
//        ModItems.register();
//        ModBlocks.register();
        ModEntities.register();
    }

    public void init(FMLInitializationEvent event) {
//        GameRegistry.registerWorldGenerator(new OverworldOreGenerator(), 0);
//        GameRegistry.registerWorldGenerator(new TabletSphereGenerator(), 100);
    }

    public void postInit(FMLPostInitializationEvent event) {
//        TabletRegistry.register();
    }
}
