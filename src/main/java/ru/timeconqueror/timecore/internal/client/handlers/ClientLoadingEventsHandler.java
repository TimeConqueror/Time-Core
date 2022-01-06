package ru.timeconqueror.timecore.internal.client.handlers;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.internal.client.TKeyBinds;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientLoadingEventsHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        TKeyBinds.registerKeys(event);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        LoadingOnlyStorage.tryLoadResourceHolders();
    }
}
