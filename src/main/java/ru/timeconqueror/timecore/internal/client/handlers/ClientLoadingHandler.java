package ru.timeconqueror.timecore.internal.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.client.render.model.TimeModelSet;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientLoadingHandler {
    public static final TimeModelSet MODEL_SET = new TimeModelSet();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LoadingOnlyStorage.tryLoadResourceHolders(); //FiXME check
    }

    @SubscribeEvent
    public static void onAddFinders(AddPackFindersEvent event) {
        event.addRepositorySource(new TimePackFinder());
    }

    @SubscribeEvent
    public static void onMinecraftConstructor(RegisterParticleProvidersEvent event) {
        ReloadableResourceManager resourceManager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        resourceManager.registerReloadListener(MODEL_SET);
    }
}
