package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;

import java.util.function.BiFunction;

public interface CapabilityFactory<T extends ICapabilityProvider, CAP_INSTANCE> extends
        BiFunction<CapabilityOwnerType<T>, T, CAP_INSTANCE> {
    @Override
    CAP_INSTANCE apply(CapabilityOwnerType<T> owner, T capProvider);
}
