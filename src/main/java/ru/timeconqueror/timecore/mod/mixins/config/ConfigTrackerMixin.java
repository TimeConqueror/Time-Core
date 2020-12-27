package ru.timeconqueror.timecore.mod.mixins.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(value = ConfigTracker.class, remap = false)
public class ConfigTrackerMixin {
    @Inject(method = "loadConfigs", at = @At(value = "HEAD"))
    public void fixPreserveInsertionOrder(ModConfig.Type type, Path configBasePath, CallbackInfo ci) {
        Config.setInsertionOrderPreserved(true);
    }
}
