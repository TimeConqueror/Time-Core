package ru.timeconqueror.timecore.mod.mixins.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.mod.common.config.MainConfig;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @ModifyVariable(method = "doLoadLevel",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            index = 11)
    private boolean disableExperimentalWarning(boolean containsExperimentalSettings) {
        if (containsExperimentalSettings) {
            TimeCore.LOGGER.info("The world contains experimental features.");

            if (MainConfig.INSTANCE.suppressExperimentalWarning.get()) {
                TimeCore.LOGGER.info("Suppressing warning about using experimental features...");

                return false;
            }
        }

        return containsExperimentalSettings;
    }
}