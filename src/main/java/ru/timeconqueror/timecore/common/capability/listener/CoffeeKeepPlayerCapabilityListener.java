package ru.timeconqueror.timecore.common.capability.listener;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public class CoffeeKeepPlayerCapabilityListener<T extends Tag> {

    private final Function<Player, INBTSerializable<T>> extractor;

    public CoffeeKeepPlayerCapabilityListener(Function<Player, INBTSerializable<T>> extractor) {
        this.extractor = extractor;
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        INBTSerializable<T> newCap = extractor.apply(event.getEntity());
        INBTSerializable<T> oldCap = extractor.apply(event.getOriginal());

        if (newCap != null && oldCap != null) {
            newCap.deserializeNBT(oldCap.serializeNBT());
        }
    }
}
