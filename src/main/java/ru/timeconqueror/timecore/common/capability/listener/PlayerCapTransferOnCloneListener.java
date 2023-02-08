package ru.timeconqueror.timecore.common.capability.listener;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public class PlayerCapTransferOnCloneListener<T extends INBT> {

    private final Function<PlayerEntity, INBTSerializable<T>> extractor;

    public PlayerCapTransferOnCloneListener(Function<PlayerEntity, INBTSerializable<T>> extractor) {
        this.extractor = extractor;
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        INBTSerializable<T> newCap = extractor.apply(event.getPlayer());
        INBTSerializable<T> oldCap = extractor.apply(event.getOriginal());

        if (newCap != null && oldCap != null) {
            newCap.deserializeNBT(oldCap.serializeNBT());
        }
    }
}
