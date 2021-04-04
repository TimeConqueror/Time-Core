package ru.timeconqueror.timecore.common.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ru.timeconqueror.timecore.common.capability.listener.CoffeeAttachCapabilityListener;
import ru.timeconqueror.timecore.common.capability.listener.CoffeeKeepPlayerCapabilityListener;
import ru.timeconqueror.timecore.common.capability.listener.CoffeeSendUpdatesCapabilityListener;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.attach.AttachableCoffeeCapability;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CoffeeCapabilityGetter;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.StaticCoffeeCapabilityGetter;
import ru.timeconqueror.timecore.common.capability.provider.IEnergyStorageProvider;
import ru.timeconqueror.timecore.common.capability.provider.IFluidHandlerProvider;
import ru.timeconqueror.timecore.common.capability.provider.IItemHandlerProvider;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CoffeeCapabilityManager {

    private final ArrayList<AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends ICoffeeCapability<?>>> attachableCapabilities = new ArrayList<>();

    public CoffeeCapabilityManager() {
        MinecraftForge.EVENT_BUS.register(new CoffeeAttachCapabilityListener());
    }

    public <T extends ICapabilityProvider, C extends ICoffeeCapability<T>> void attachCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        attachCapability(owner, capability, predicate, getters);
        attachableCapabilities.add(new AttachableCoffeeCapability<>(owner, capability));
    }

    public <T extends ICapabilityProvider, C> void attachCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, getters));
    }

    public <T extends ICapabilityProvider, C extends ICoffeeCapability<T>> void attachStaticCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> factory) {
        attachStaticCapability(owner, capability, predicate, factory);
        attachableCapabilities.add(new AttachableCoffeeCapability<>(owner, capability));
    }

    public <T extends ICapabilityProvider, C> void attachStaticCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> factory) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, () -> new StaticCoffeeCapabilityGetter<>(factory)));
    }

    public void addDefaultAttachers() {
        attachCapability(CapabilityOwner.TILE_ENTITY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (tile) -> tile instanceof IItemHandlerProvider,
                ((CoffeeCapabilityGetter<TileEntity, IItemHandler>) (target, facing) -> ((IItemHandlerProvider) target).getItemHandler(facing)).supply());

        attachCapability(CapabilityOwner.TILE_ENTITY, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (tile) -> tile instanceof IFluidHandlerProvider,
                ((CoffeeCapabilityGetter<TileEntity, IFluidHandler>) (target, facing) -> ((IFluidHandlerProvider) target).getFluidHandler(facing)).supply());

        attachCapability(CapabilityOwner.TILE_ENTITY, CapabilityEnergy.ENERGY, (tile) -> tile instanceof IEnergyStorageProvider,
                ((CoffeeCapabilityGetter<TileEntity, IEnergyStorage>) (target, facing) -> ((IEnergyStorageProvider) target).getEnergyStorage(facing)).supply());
    }

    public <T extends INBT> void enableKeepingPlayerCapability(Function<PlayerEntity, INBTSerializable<T>> extractor) {
        MinecraftForge.EVENT_BUS.register(new CoffeeKeepPlayerCapabilityListener<>(extractor));
    }

    public void enableSendingPlayerCapabilityUpdates(Consumer<PlayerEntity> onUpdate) {
        MinecraftForge.EVENT_BUS.register(new CoffeeSendUpdatesCapabilityListener(onUpdate));
    }

    public AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends ICoffeeCapability<?>> getAttachableCoffeeCapability(String name) {
        for (AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends ICoffeeCapability<?>> capability : attachableCapabilities) {
            if (capability.getCapability().getName().equals(name)) {
                return capability;
            }
        }
        return null;
    }
}
