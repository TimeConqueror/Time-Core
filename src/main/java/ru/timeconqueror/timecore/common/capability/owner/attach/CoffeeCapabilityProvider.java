package ru.timeconqueror.timecore.common.capability.owner.attach;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CoffeeCapabilityGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CoffeeCapabilityProvider<T> implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    private final T target;
    private final HashMap<String, CoffeeCapabilityGetter<T, ?>> getters = new HashMap<>();

    public CoffeeCapabilityProvider(T target) {
        this.target = target;
    }

    public <C> void addCapability(Capability<C> capability, CoffeeCapabilityGetter<T, C> getter) {
        getters.put(capability.getName(), getter);
        getter.getCapability(target, null);
    }

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction side) {
        CoffeeCapabilityGetter<T, ?> getter = getters.get(capability.getName());

        if (getter != null) {
            C cap = (C) getter.getCapability(target, side);

            if (cap != null) {
                return LazyOptional.of(new NonNullSupplier<C>() {
                    @Nonnull
                    @Override
                    public C get() {
                        return cap;
                    }
                });
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT root = new CompoundNBT();

        for (Map.Entry<String, CoffeeCapabilityGetter<T, ?>> entry : getters.entrySet()) {
            Object cap = entry.getValue().getCapability(target, null);

            if (cap instanceof INBTSerializable) {
                root.put(entry.getKey(), ((INBTSerializable<INBT>) cap).serializeNBT());
            }
        }

        return root;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for (Map.Entry<String, CoffeeCapabilityGetter<T, ?>> entry : getters.entrySet()) {
            Object cap = entry.getValue().getCapability(target, null);

            if (cap instanceof INBTSerializable && nbt.contains(entry.getKey())) {
                CompoundNBT comp = nbt.getCompound(entry.getKey());
                ((INBTSerializable<INBT>) cap).deserializeNBT(comp);
            }
        }
    }
}
