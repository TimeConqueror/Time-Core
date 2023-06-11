package ru.timeconqueror.timecore.common.capability.listener;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityCapSyncOnStartTrackListener {
    private final SyncFunction syncFunction;

    public EntityCapSyncOnStartTrackListener(SyncFunction syncFunction) {
        this.syncFunction = syncFunction;
    }

    @SubscribeEvent
    public void startTracking(PlayerEvent.StartTracking event) {
        if (!event.getEntity().level().isClientSide) {
            syncFunction.onStartTracking(event.getEntity(), event.getTarget());
        }
    }

    public interface SyncFunction {
        /**
         * Target's capability may be null at this point!
         *
         * @param player player which start tracking entity target
         * @param target entity which is started tracking by provided player
         */
        void onStartTracking(Player player, Entity target);
    }
}
