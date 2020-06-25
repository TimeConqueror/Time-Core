package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.StateMachine;

@Mod.EventBusSubscriber
public class AnimationEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        if (entityLiving instanceof AnimationProvider<?>) {
            if (entityLiving.isServerWorld()) {
                //needed for animation ticking on server side.
                ((AnimationProvider<?>) entityLiving).getStateMachine().getAnimationManager().applyAnimations(DummyElements.DUMMY_ENTITY_MODEL);
            } else {
                StateMachine<?> stateMachine = ((AnimationProvider<?>) entityLiving).getStateMachine();
                stateMachine.onTick();
            }
        }
    }
}
