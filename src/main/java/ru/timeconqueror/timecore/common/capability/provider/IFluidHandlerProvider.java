package ru.timeconqueror.timecore.common.capability.provider;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public interface IFluidHandlerProvider {

    IFluidHandler getFluidHandler(@Nullable Direction direction);

    default boolean hasFluidHandler(@Nullable Direction direction) {
        return getFluidHandler(direction) != null;
    }
}
