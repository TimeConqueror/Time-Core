package ru.timeconqueror.timecore.mixins.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.mod.common.config.MainConfig;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @ModifyVariable(method = "loadWorld",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            index = 13, // flag1
            remap = false)
    private boolean suppressExperimentalWarning(boolean containsExperimentalFeatures) {
        if (containsExperimentalFeatures) {
            TimeCore.LOGGER.info("The world contains experimental features.");

            if (MainConfig.INSTANCE.suppressExperimentalWarning.get()) {
                TimeCore.LOGGER.info("Suppressing warning about using experimental features...");

                return false;
            }
        }

        return containsExperimentalFeatures;
    }
}