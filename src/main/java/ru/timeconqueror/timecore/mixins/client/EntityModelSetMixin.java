package ru.timeconqueror.timecore.mixins.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin {
    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void reloadTimeModelSet(ResourceManager resourceManager, CallbackInfo ci) {
        ClientLoadingHandler.MODEL_SET.onResourceManagerReload(resourceManager);
    }
}
