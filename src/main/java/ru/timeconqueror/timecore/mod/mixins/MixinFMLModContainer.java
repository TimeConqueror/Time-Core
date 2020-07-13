package ru.timeconqueror.timecore.mod.mixins;

import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.mod.misc.ModInitializer;

@Mixin(value = FMLModContainer.class, remap = false)
public abstract class MixinFMLModContainer {

    @Inject(method = "constructMod",
            at = @At(value = "TAIL")
    )
    public void fmlModConstructingHook(LifecycleEventProvider.LifecycleEvent event, CallbackInfo ci) {
        FMLModContainer modContainer = (FMLModContainer) (Object) this;
        Object mod = modContainer.getMod();
        if (mod instanceof TimeMod) {
            ModInitializer.run();
        }

        FMLJavaModLoadingContext.get().getModEventBus().post(new FMLModConstructedEvent(modContainer));
    }
}
