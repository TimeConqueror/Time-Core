package ru.timeconqueror.timecore.common.capability.owner.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityOwnerSerializer<T extends ICapabilityProvider> {

    void serializeOwner(Level world, T owner, CompoundTag nbt);

    T deserializeOwner(Level world, CompoundTag nbt);
}
