package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.event.LivingTickEndEvent;

//TODO add tickers for tile entities
@EventBusSubscriber
public class DefaultAnimationSystemCallers {
    @SubscribeEvent
    public static void onEntityTickEnd(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof AnimatedObject<?> animated) {
            //needed for animation ticking on server side.
            animated.animationSystem().onTick(entity.level().isClientSide);
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        Entity target = event.getTarget();
        if (target instanceof AnimatedObject<?> animatedObj) {
            animatedObj.animationSystem().syncForPlayer(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onChunkTrackingStart(ChunkWatchEvent.Sent event) {
        ServerLevel level = event.getLevel();
        ChunkPos chunkPos = event.getPos();
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        for (BlockEntity entity : chunk.getBlockEntities().values()) {
            if (entity instanceof AnimatedObject<?> animatedObj) {
                animatedObj.animationSystem().syncForPlayer(event.getPlayer());
            }
        }
    }
}
