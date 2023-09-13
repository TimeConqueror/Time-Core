package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.common.event.LivingTickEndEvent;
import ru.timeconqueror.timecore.api.util.holder.Pair;

//TODO add tickers for tile entities
@Mod.EventBusSubscriber
public class DefaultAnimationSystemCallers {
    public static void onEntityTickEnd(LivingTickEndEvent event) {
        LivingEntity living = event.getEntity();

        if (living instanceof AnimatedObject<?> animated) {
            //needed for animation ticking on server side.
            animated.getSystem().onTick(living.level().isClientSide);
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target instanceof AnimatedObject<?> animatedObj) {
            AnimationManager animationManager = animatedObj.getSystem().getAnimationManager();
            var tickersByLayer = animationManager.getLayerNames().stream()
                    .map(animationManager::getLayer)
                    .map(layer -> Pair.of(layer.getName(), layer.getCurrentTicker()))
                    .toList();
            animatedObj.getSystem().getNetworkDispatcher().sendSyncAnimationsPacket(tickersByLayer);
        }
    }
}
