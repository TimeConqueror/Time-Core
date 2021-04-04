package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.Capability;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CoffeeCapabilityGetter;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CoffeeCapabilityAttacher<T, C> {

    private final Capability<C> capability;
    private final Predicate<T> predicate;
    Supplier<CoffeeCapabilityGetter<T, C>> getters;

    public CoffeeCapabilityAttacher(Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        this.capability = capability;
        this.predicate = predicate;
        this.getters = getters;
    }

    public Capability<C> getCapability() {
        return capability;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public Supplier<CoffeeCapabilityGetter<T, C>> getGetters() {
        return getters;
    }
}
