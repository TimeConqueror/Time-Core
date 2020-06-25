package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

@Mod.EventBusSubscriber
public class AnimationEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();
        if (entityLiving.isServerWorld() && entityLiving instanceof AnimationProvider) {
            ((AnimationProvider<?>) entityLiving).getStateMachine().getAnimationManager().applyAnimations(DummyElements.DUMMY_ENTITY_MODEL);
        }
    }
}
