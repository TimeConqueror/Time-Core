package ru.timeconqueror.timecore.mixins;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.internal.common.TimeEventHooks;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void onLivingTickEnd(CallbackInfo ci) {
        TimeEventHooks.onLivingUpdateEnd((LivingEntity) (Object) this);
    }
}
