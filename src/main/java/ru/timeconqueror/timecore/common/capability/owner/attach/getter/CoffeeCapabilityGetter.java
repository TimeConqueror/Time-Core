package ru.timeconqueror.timecore.common.capability.owner.attach.getter;

import net.minecraft.util.Direction;

import java.util.function.Supplier;

public interface CoffeeCapabilityGetter<T, C> {

    C getCapability(T target, Direction direction);

    default Supplier<CoffeeCapabilityGetter<T, C>> supply() {
        return () -> this;
    }

}
