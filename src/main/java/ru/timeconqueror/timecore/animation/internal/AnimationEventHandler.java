package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.ServerAnimationManager;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CSyncEntityAnimationsMsg;

@Mod.EventBusSubscriber
public class AnimationEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        if (entityLiving instanceof AnimationProvider<?>) {
            if (entityLiving.isServerWorld()) {
                //needed for animation ticking on server side.
                ((AnimationProvider<?>) entityLiving).getActionManager().getAnimationManager().applyAnimations(null);
            } else {
                ActionManager<?> actionManager = ((AnimationProvider<?>) entityLiving).getActionManager();
                actionManager.onTick();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target instanceof AnimationProvider<?>) {
            AnimationManager animationManager = ((AnimationProvider<?>) target).getActionManager().getAnimationManager();
            ServerAnimationManager<?> serverAnimationManager = (ServerAnimationManager<?>) animationManager;
            InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayerEntity) event.getTarget())), new S2CSyncEntityAnimationsMsg(serverAnimationManager, target));
        }
    }
}
