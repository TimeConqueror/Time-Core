package ru.timeconqueror.timecore.common.capability;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import ru.timeconqueror.timecore.common.capability.listener.CoffeeKeepPlayerCapabilityListener;
import ru.timeconqueror.timecore.common.capability.listener.CoffeeOnPlayerJoinedSendCapabilityListener;
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

    private final ArrayList<AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapability<?>>> attachableCapabilities = new ArrayList<>();

    public <T extends ICapabilityProvider, C extends CoffeeCapability<T>> void attachDynamicCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        attachDynamicCapability(owner, capability, predicate, getters);
        attachableCapabilities.add(new AttachableCoffeeCapability<>(owner, capability));
    }

    public <T extends ICapabilityProvider, C> void attachDynamicCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, getters));
    }

    public <T extends ICapabilityProvider, C extends CoffeeCapability<T>> void attachStaticCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> capFactory) {
        attachStaticCapability(owner, capability, predicate, capFactory);
        attachableCapabilities.add(new AttachableCoffeeCapability<>(owner, capability));
    }

    public <T extends ICapabilityProvider, C> void attachStaticCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> factory) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, () -> new StaticCoffeeCapabilityGetter<>(factory)));
    }

    public void addDefaultAttachers() {
        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (tile) -> tile instanceof IItemHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IItemHandler>) (target, facing) -> ((IItemHandlerProvider) target).getItemHandler(facing)).supply());

        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (tile) -> tile instanceof IFluidHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IFluidHandler>) (target, facing) -> ((IFluidHandlerProvider) target).getFluidHandler(facing)).supply());

        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, CapabilityEnergy.ENERGY, (tile) -> tile instanceof IEnergyStorageProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IEnergyStorage>) (target, facing) -> ((IEnergyStorageProvider) target).getEnergyStorage(facing)).supply());
    }

    /**
     * Enables automatic capability transfer on player death
     *
     * @param capabilityExtractor function, which should extract needed capability from the player
     */
    public <T extends Tag> void enableKeepingPlayerCapability(Function<Player, INBTSerializable<T>> capabilityExtractor) {
        MinecraftForge.EVENT_BUS.register(new CoffeeKeepPlayerCapabilityListener<>(capabilityExtractor));
    }

    /**
     * Enables automatic capability syncing on such actions as:
     * <ol>
     *  <li>Player joined the world</li>
     *  <li>Player changed the world</li>
     *  <li>Player respawned</li>
     * </ol>
     *
     * @param onSyncRequest function, which should send needed data to the client
     */
    public void enableSyncingPlayerCapabilityOnJoin(Consumer<Player> onSyncRequest) {
        MinecraftForge.EVENT_BUS.register(new CoffeeOnPlayerJoinedSendCapabilityListener(onSyncRequest));
    }

    public AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapability<?>> getAttachableCoffeeCapability(String name) {
        for (AttachableCoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapability<?>> capability : attachableCapabilities) {
            if (capability.getCapability().getName().equals(name)) {
                return capability;
            }
        }
        return null;
    }
}
