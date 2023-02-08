package ru.timeconqueror.timecore.common.capability;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.common.capability.listener.EntityCapSyncOnStartTrackListener;
import ru.timeconqueror.timecore.common.capability.listener.PlayerCapSyncOnJoinListener;
import ru.timeconqueror.timecore.common.capability.listener.PlayerCapTransferOnCloneListener;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapability;
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

public class CapabilityManager {

    private final ArrayList<CoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapabilityInstance<?>>> attachableCapabilities = new ArrayList<>();

    public <T extends ICapabilityProvider, C extends CoffeeCapabilityInstance<T>> void registerDynamicCoffeeAttacher(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> ownerFilter, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        registerDynamicCapabilityAttacher(owner, capability, ownerFilter, getters);
        attachableCapabilities.add(new CoffeeCapability<>(owner, capability));
    }

    /**
     * Adds the capability, which is based on provided direction.
     * So the getter can return different capabilities based on direction, as it works for ItemHandler in tile entities for example.
     */
    public <T extends ICapabilityProvider, C> void registerDynamicCapabilityAttacher(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> ownerFilter, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, ownerFilter, getters));
    }

    public <T extends ICapabilityProvider, C extends CoffeeCapabilityInstance<T>> void registerStaticCoffeeCapabilityAttacher(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> ownerFilter, Function<T, C> capFactory) {
        registerStaticCapabilityAttacher(owner, capability, ownerFilter, capFactory);
        attachableCapabilities.add(new CoffeeCapability<>(owner, capability));
    }

    /**
     * Adds the capability, which is single and permanent for the provided target.
     */
    public <T extends ICapabilityProvider, C> void registerStaticCapabilityAttacher(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> ownerFilter, Function<T, C> capFactory) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, ownerFilter, () -> new StaticCoffeeCapabilityGetter<>(capFactory)));
    }

    public void addDefaultAttachers() {
        registerDynamicCapabilityAttacher(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.ITEM_HANDLER, tile -> tile instanceof IItemHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IItemHandler>) (target, facing) -> ((IItemHandlerProvider) target).getItemHandler(facing)).supply());

        registerDynamicCapabilityAttacher(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.FLUID_HANDLER, tile -> tile instanceof IFluidHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IFluidHandler>) (target, facing) -> ((IFluidHandlerProvider) target).getFluidHandler(facing)).supply());

        registerDynamicCapabilityAttacher(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.ENERGY, tile -> tile instanceof IEnergyStorageProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IEnergyStorage>) (target, facing) -> ((IEnergyStorageProvider) target).getEnergyStorage(facing)).supply());
    }

    /**
     * Enables automatic capability transfer on player death
     *
     * @param capabilityExtractor function, which should extract needed capability from the player
     */
    public <T extends Tag> void makePlayerCapTransferOnClone(Function<Player, INBTSerializable<T>> capabilityExtractor) {
        MinecraftForge.EVENT_BUS.register(new PlayerCapTransferOnCloneListener<>(capabilityExtractor));
    }

    public void makePlayerCapSyncOnJoin(Consumer<Player> onSyncRequest) {
        MinecraftForge.EVENT_BUS.register(new PlayerCapSyncOnJoinListener(onSyncRequest));
    }

    public void makeEntityCapSyncOnStartTracking(EntityCapSyncOnStartTrackListener.SyncFunction onSyncRequest) {
        MinecraftForge.EVENT_BUS.register(new EntityCapSyncOnStartTrackListener(onSyncRequest));
    }

    @Nullable
    public CoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapabilityInstance<?>> getAttachableCoffeeCapability(String name) {
        for (CoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapabilityInstance<?>> capability : attachableCapabilities) {
            if (capability.capability().getName().equals(name)) {
                return capability;
            }
        }
        return null;
    }
}
