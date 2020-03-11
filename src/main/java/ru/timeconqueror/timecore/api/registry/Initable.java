package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Deprecated
public interface Initable {
    void onInit(FMLCommonSetupEvent event);
}
