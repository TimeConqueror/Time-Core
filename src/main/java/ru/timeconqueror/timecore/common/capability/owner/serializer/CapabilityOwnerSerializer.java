package ru.timeconqueror.timecore.common.capability.owner.serializer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityOwnerSerializer<T extends ICapabilityProvider> {

    void serializeOwner(World world, T owner, CompoundNBT nbt);

    T deserializeOwner(World world, CompoundNBT nbt);
}
