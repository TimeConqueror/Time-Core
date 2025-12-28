package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.Capability;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CapabilityProviderAdapter;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record CoffeeCapabilityAttacher<T, C>(Capability<C> capability,
                                             Predicate<? super T> predicate,
                                             Supplier<CapabilityProviderAdapter<T, C>> getterFactory) {
}
