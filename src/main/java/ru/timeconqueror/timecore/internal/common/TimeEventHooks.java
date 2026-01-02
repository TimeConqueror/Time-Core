package ru.timeconqueror.timecore.internal.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForge;
import ru.timeconqueror.timecore.animation.internal.DefaultAnimationSystemCallers;
import ru.timeconqueror.timecore.api.common.event.LivingTickEndEvent;
//TODO Remove?
public class TimeEventHooks {
//    public static void onLivingUpdateEnd(LivingEntity entity) {
//        LivingTickEndEvent event = new LivingTickEndEvent(entity);
//        DefaultAnimationSystemCallers.onEntityTickEnd(event);
//
//        NeoForge.EVENT_BUS.post(event);
//    }

//    public static void onChunkTrackingStart(ServerPlayer player, LevelChunk chunk) {
//        DefaultAnimationSystemCallers.onChunkTrackingStart(player, chunk);
//    }
}
