package ru.timeconqueror.timecore.common.capability.listener;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Consumer;

public class CoffeeOnPlayerJoinedSendCapabilityListener {
    private final Consumer<Player> onUpdate;

    public CoffeeOnPlayerJoinedSendCapabilityListener(Consumer<Player> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @SubscribeEvent
    public void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        onUpdate.accept(event.getPlayer());
    }

    @SubscribeEvent
    public void onChangeWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
        onUpdate.accept(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!e.getPlayer().level.isClientSide()) {
            onUpdate.accept(e.getPlayer());
        }
    }
}
