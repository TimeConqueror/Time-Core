package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.Capability;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CoffeeCapabilityGetter;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record CoffeeCapabilityAttacher<T, C>(Capability<C> capability,
                                             Predicate<T> predicate,
                                             Supplier<CoffeeCapabilityGetter<T, C>> getterFactory) {

}
