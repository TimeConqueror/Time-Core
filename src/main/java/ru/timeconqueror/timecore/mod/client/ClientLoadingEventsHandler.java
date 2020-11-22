package ru.timeconqueror.timecore.mod.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientLoadingEventsHandler {
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        LoadingOnlyStorage.loadResourceHolders();
    }
}
