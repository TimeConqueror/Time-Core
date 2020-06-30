package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

@Mod.EventBusSubscriber
public class AnimationEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        if (entityLiving instanceof AnimationProvider<?>) {
            if (entityLiving.isServerWorld()) {
                //needed for animation ticking on server side.
                ((AnimationProvider<?>) entityLiving).getActionManager().getAnimationManager().applyAnimations(null);
            } else {
                ActionManager<?> actionManager = ((AnimationProvider<?>) entityLiving).getActionManager();
                actionManager.onTick();
            }
        }
    }
}
