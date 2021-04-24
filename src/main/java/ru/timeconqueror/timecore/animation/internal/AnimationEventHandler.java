package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.animation.ServerAnimationManager;
import ru.timeconqueror.timecore.animation.action.EntityActionManager;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.common.event.LivingUpdateEndEvent;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.animation.S2CSyncAnimationsMsg;

@Mod.EventBusSubscriber
public class AnimationEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        if (entityLiving instanceof AnimatedObject<?>) {
            if (entityLiving.isEffectiveAi()) {
                //needed for animation ticking on server side.
                ((AnimatedObject<?>) entityLiving).getSystem().getAnimationManager().applyAnimations(null);
            }
        }
    }

    public static void onEntityTickEnd(LivingUpdateEndEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        if (entityLiving instanceof AnimatedObject<?>) {
            EntityActionManager<?> actionManager = (EntityActionManager<?>) (((AnimatedObject<?>) entityLiving).getSystem().getActionManager());
            actionManager.onTick();
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target instanceof AnimatedObject<?>) {
            AnimationManager animationManager = ((AnimatedObject<?>) target).getSystem().getActionManager().getAnimationManager();
            ServerAnimationManager<?> serverAnimationManager = (ServerAnimationManager<?>) animationManager;
            InternalPacketManager.sendToPlayer(((ServerPlayerEntity) event.getPlayer()), new S2CSyncAnimationsMsg(serverAnimationManager, target));
        }
    }
}
