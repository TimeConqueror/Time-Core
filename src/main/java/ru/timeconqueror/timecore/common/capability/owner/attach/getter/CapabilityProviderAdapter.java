package ru.timeconqueror.timecore.common.capability.owner.attach.getter;

import net.minecraft.core.Direction;

import java.util.function.Supplier;

public interface CapabilityProviderAdapter<T, C> {

    C getCapability(T target, Direction direction);

    default Supplier<CapabilityProviderAdapter<T, C>> supply() {
        return () -> this;
    }

}
