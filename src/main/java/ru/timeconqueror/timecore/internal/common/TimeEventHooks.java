package ru.timeconqueror.timecore.internal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import ru.timeconqueror.timecore.animation.internal.AnimationSystemCallers;
import ru.timeconqueror.timecore.api.common.event.LivingTickEndEvent;

public class TimeEventHooks {
    public static void onLivingUpdateEnd(LivingEntity entity) {
        LivingTickEndEvent event = new LivingTickEndEvent(entity);
        AnimationSystemCallers.onEntityTickEnd(event);

        MinecraftForge.EVENT_BUS.post(event);
    }
}
