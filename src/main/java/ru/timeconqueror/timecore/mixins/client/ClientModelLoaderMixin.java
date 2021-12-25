package ru.timeconqueror.timecore.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.fml.client.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.Markers;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;

@Mixin(value = ClientModLoader.class, remap = false)
public abstract class ClientModelLoaderMixin {
    @Inject(method = "begin", at = @At(value = "TAIL"))
    private static void injectCustomPack(Minecraft minecraft, PackRepository defaultResourcePacks, ReloadableResourceManager mcResourceManager, ClientPackSource metadataSerializer, CallbackInfo ci) {
        TimeCore.LOGGER.debug(Markers.RESOURCE_SYSTEM, "Adding TimePackFinder to resourcepack list.");
        defaultResourcePacks.addPackFinder(new TimePackFinder());
    }
}
