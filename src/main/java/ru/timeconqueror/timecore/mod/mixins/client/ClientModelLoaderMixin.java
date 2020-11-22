package ru.timeconqueror.timecore.mod.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.client.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;
import ru.timeconqueror.timecore.mod.misc.Markers;

@Mixin(value = ClientModLoader.class, remap = false)
public abstract class ClientModelLoaderMixin {
    @Inject(method = "begin", at = @At(value = "TAIL"))
    private static void injectCustomPack(Minecraft minecraft, ResourcePackList defaultResourcePacks, IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer, CallbackInfo ci) {
        TimeCore.LOGGER.debug(Markers.RESOURCE_SYSTEM, "Adding TimePackFinder to resourcepack list.");
        defaultResourcePacks.addPackFinder(new TimePackFinder());
    }
}
