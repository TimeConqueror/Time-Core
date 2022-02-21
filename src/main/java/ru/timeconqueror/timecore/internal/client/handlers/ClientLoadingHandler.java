package ru.timeconqueror.timecore.internal.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.client.render.model.TimeModelSet;
import ru.timeconqueror.timecore.internal.client.TKeyBinds;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientLoadingHandler {
    public static final TimeModelSet MODEL_SET = new TimeModelSet();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        TKeyBinds.registerKeys(event);
    }

    @SubscribeEvent
    public static void onMinecraftConstructor(ParticleFactoryRegisterEvent event) {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        resourceManager.registerReloadListener(MODEL_SET);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        LoadingOnlyStorage.tryLoadResourceHolders();
    }
}
