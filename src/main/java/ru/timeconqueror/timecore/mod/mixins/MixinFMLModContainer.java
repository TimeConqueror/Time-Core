package ru.timeconqueror.timecore.mod.mixins;

import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.mod.misc.ModInitializer;

@Mixin(value = FMLModContainer.class, remap = false)
public abstract class MixinFMLModContainer {

    @Shadow
    @Final
    private ModFileScanData scanResults;
    @Shadow
    @Final
    private Class<?> modClass;

    @Inject(method = "constructMod",
            at = @At(value = "TAIL")
    )
    public void fmlModConstructingHook(LifecycleEventProvider.LifecycleEvent event, CallbackInfo ci) {
        FMLModContainer modContainer = (FMLModContainer) (Object) this;
        Object mod = getMod();
        String modId = modContainer.getModId();

        if (mod instanceof TimeMod) {
            ModInitializer.run(modId, modContainer, scanResults, modClass);
        }

        FMLJavaModLoadingContext.get().getModEventBus().post(new FMLModConstructedEvent(modContainer));
    }

    @Shadow
    public abstract Object getMod();
}
