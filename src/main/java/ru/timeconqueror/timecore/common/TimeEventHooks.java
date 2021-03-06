package ru.timeconqueror.timecore.common;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import ru.timeconqueror.timecore.animation.internal.AnimationEventHandler;
import ru.timeconqueror.timecore.api.common.event.LivingUpdateEndEvent;

public class TimeEventHooks {
    public static void onLivingUpdateEnd(LivingEntity entity) {
        LivingUpdateEndEvent event = new LivingUpdateEndEvent(entity);
        AnimationEventHandler.onEntityTickEnd(event);

        MinecraftForge.EVENT_BUS.post(event);
    }
}
