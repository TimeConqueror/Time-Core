package ru.timeconqueror.timecore.internal.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.render.model.TimeModelSet;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;
import ru.timeconqueror.timecore.client.resource.TimePackResources;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

@EventBusSubscriber(value = Dist.CLIENT)
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
