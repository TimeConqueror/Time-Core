package ru.timeconqueror.timecore.common.capability.owner.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityOwnerCodec<T extends ICapabilityProvider> {

    void serialize(Level world, T owner, CompoundTag nbt);

    T deserialize(Level world, CompoundTag nbt);
}
