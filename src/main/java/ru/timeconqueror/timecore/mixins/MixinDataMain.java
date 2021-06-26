package ru.timeconqueror.timecore.mixins;

import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedField;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;

@Mixin(value = Main.class, remap = false)
public abstract class MixinDataMain {
    @Inject(method = "main",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/DatagenModLoader;begin(Ljava/util/Set;Ljava/nio/file/Path;Ljava/util/Collection;Ljava/util/Collection;Ljava/util/Set;ZZZZZZ)V"))
    private static void onStart(String[] args, CallbackInfo ci) {
        UnlockedField<Boolean> fIsInDataMode = ReflectionHelper.findField(EnvironmentUtils.class, "isInDataMode");
        fIsInDataMode.set(null, true);
    }
}
