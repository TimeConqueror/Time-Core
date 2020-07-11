package ru.timeconqueror.timecore.registry;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Deprecated
public interface Initable {
    void onInit(FMLCommonSetupEvent event);
}
