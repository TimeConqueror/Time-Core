package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ru.timeconqueror.timecore.common.capability.ICoffeeCapability;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;

public class AttachableCoffeeCapability<T extends ICapabilityProvider, C extends ICoffeeCapability<T>> {

    private final CapabilityOwner<T> owner;
    private final Capability<C> capability;

    public AttachableCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability) {
        this.owner = owner;
        this.capability = capability;
    }

    public Capability<C> getCapability() {
        return capability;
    }

    public CapabilityOwner<T> getOwner() {
        return owner;
    }
}
