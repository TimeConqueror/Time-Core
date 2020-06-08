package ru.timeconqueror.timecore.mixins.inject;

import net.minecraft.data.DirectoryCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.timeconqueror.timecore.api.datagen.DataGen;

import java.io.IOException;

@Mixin(DirectoryCache.class)
public abstract class MixinDirectoryCache {
    @Redirect(method = "writeCache", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/DirectoryCache;deleteStale()V"))
    public void disableFileDeletionIfOff(DirectoryCache directoryCache) throws IOException {
        if (!DataGen.disableFileDeletion) {
            deleteStale();
        }
    }

    @Shadow
    protected abstract void deleteStale() throws IOException;
}
