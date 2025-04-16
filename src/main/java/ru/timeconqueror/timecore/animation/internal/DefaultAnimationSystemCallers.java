package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.event.LivingTickEndEvent;

//TODO add tickers for tile entities
@Mod.EventBusSubscriber
public class DefaultAnimationSystemCallers {
    public static void onEntityTickEnd(LivingTickEndEvent event) {
        LivingEntity living = event.getEntity();

        if (living instanceof AnimatedObject<?> animated) {
            //needed for animation ticking on server side.
            animated.animationSystem().onTick(living.level().isClientSide);
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

    public static void onChunkTrackingStart(ServerPlayer player, LevelChunk chunk) {
        for (BlockEntity entity : chunk.getBlockEntities().values()) {
            if (entity instanceof AnimatedObject<?> animatedObj) {
                animatedObj.animationSystem().syncForPlayer(player);
            }
        }
    }
}
