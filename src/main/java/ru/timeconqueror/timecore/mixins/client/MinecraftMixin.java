package ru.timeconqueror.timecore.mixins.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)//TODO check if it works in default runtime
public abstract class MinecraftMixin {
    //FIXME
    //  @SuppressWarnings("InvalidInjectorMethodSignature")
//    @ModifyVariable(method = "doLoadLevel",
//            at = @At(
//                    value = "STORE",
//                    ordinal = 0
//            ),
//            index = 13, // setting the flag1
//            remap = false)
//    private boolean suppressExperimentalWarning(boolean containsExperimentalFeatures) {
//        if (containsExperimentalFeatures) {
//            TimeCore.LOGGER.info("The world contains experimental features.");
//
//            if (MainConfig.INSTANCE.suppressExperimentalWarning.get()) {
//                TimeCore.LOGGER.info("Suppressing warning about using experimental features...");
//
//                return false;
//            }
//        }
//
//        return containsExperimentalFeatures;
    // }
}