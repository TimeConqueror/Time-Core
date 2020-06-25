package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.StateMachine;

@Mod.EventBusSubscriber
public class LivingEventsHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote) {
            LivingEntity entityLiving = event.getEntityLiving();

            if (entityLiving instanceof AnimationProvider<?>) {
                StateMachine<?> stateMachine = ((AnimationProvider<?>) entityLiving).getStateMachine();
                stateMachine.onTick();
            }
        }
    }
}
