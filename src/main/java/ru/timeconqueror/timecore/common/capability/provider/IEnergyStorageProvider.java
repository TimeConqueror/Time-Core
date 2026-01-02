package ru.timeconqueror.timecore.common.capability.provider;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyStorageProvider {

    IEnergyStorage getEnergyStorage(@Nullable Direction direction);

    default boolean hasEnergyStorage(@Nullable Direction direction) {
        return getEnergyStorage(direction) != null;
    }
}
