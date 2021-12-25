package ru.timeconqueror.timecore.common.capability.provider;

import net.minecraft.core.Direction;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IItemHandlerProvider {

    IItemHandler getItemHandler(@Nullable Direction direction);

    default boolean hasItemHandler(@Nullable Direction direction) {
        return getItemHandler(direction) != null;
    }

}
