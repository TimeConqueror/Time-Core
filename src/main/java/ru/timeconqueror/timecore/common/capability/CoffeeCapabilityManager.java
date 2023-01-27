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
import ru.timeconqueror.timecore.common.capability.listener.CoffeeKeepPlayerCapabilityListener;
import ru.timeconqueror.timecore.common.capability.listener.CoffeeOnPlayerJoinedSendCapabilityListener;
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

public class CoffeeCapabilityManager {

    private final ArrayList<CoffeeCapability<? extends ICapabilityProvider, ? extends CoffeeCapabilityInstance<?>>> attachableCapabilities = new ArrayList<>();

    public <T extends ICapabilityProvider, C extends CoffeeCapabilityInstance<T>> void attachDynamicCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        attachDynamicCapability(owner, capability, predicate, getters);
        attachableCapabilities.add(new CoffeeCapability<>(owner, capability));
    }

    /**
     * Adds the capability, which is based on provided direction.
     * So the getter can return different capabilities based on direction, as it works for ItemHandler in tile entities for example.
     */
    public <T extends ICapabilityProvider, C> void attachDynamicCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Supplier<CoffeeCapabilityGetter<T, C>> getters) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, getters));
    }

    public <T extends ICapabilityProvider, C extends CoffeeCapabilityInstance<T>> void attachStaticCoffeeCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> capFactory) {
        attachStaticCapability(owner, capability, predicate, capFactory);
        attachableCapabilities.add(new CoffeeCapability<>(owner, capability));
    }

    /**
     * Adds the capability, which is single and permanent for the provided target.
     * Factory is called once per owner and then is cached.
     */
    public <T extends ICapabilityProvider, C> void attachStaticCapability(CapabilityOwner<T> owner, Capability<C> capability, Predicate<T> predicate, Function<T, C> factory) {
        owner.getAttachers().add(new CoffeeCapabilityAttacher<>(capability, predicate, () -> new StaticCoffeeCapabilityGetter<>(factory)));
    }

    public void addDefaultAttachers() {
        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.ITEM_HANDLER, tile -> tile instanceof IItemHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IItemHandler>) (target, facing) -> ((IItemHandlerProvider) target).getItemHandler(facing)).supply());

        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.FLUID_HANDLER, tile -> tile instanceof IFluidHandlerProvider,
                ((CoffeeCapabilityGetter<BlockEntity, IFluidHandler>) (target, facing) -> ((IFluidHandlerProvider) target).getFluidHandler(facing)).supply());

        attachDynamicCapability(CapabilityOwner.TILE_ENTITY, ForgeCapabilities.ENERGY, tile -> tile instanceof IEnergyStorageProvider,
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
