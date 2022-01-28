package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;

public record CoffeeCapability<T extends ICapabilityProvider, C extends CoffeeCapabilityInstance<T>>(
        CapabilityOwner<T> owner,
        Capability<C> capability) {

}
