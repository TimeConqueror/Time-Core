package ru.timeconqueror.timecore.common.capability.owner.attach.getter;

import net.minecraft.core.Direction;

import java.util.function.Function;

public class DirectionIndependentCapabilityProvider<T, C> implements CapabilityProviderAdapter<T, C> {

    private final Function<T, C> factory;
    private C cap = null;

    public DirectionIndependentCapabilityProvider(Function<T, C> factory) {
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
