package ru.timeconqueror.timecore.common.capability.owner.attach.getter;

import net.minecraft.util.Direction;

import java.util.function.Function;

public class StaticCoffeeCapabilityGetter<T, C> implements CoffeeCapabilityGetter<T, C> {

    private final Function<T, C> factory;
    private C cap = null;

    public StaticCoffeeCapabilityGetter(Function<T, C> factory) {
        this.factory = factory;
    }

    @Override
    public C getCapability(T target, Direction direction) {
        if (cap == null) {
            cap = factory.apply(target);
        }

        return cap;
    }
}
