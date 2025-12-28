package ru.timeconqueror.timecore.common.capability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.container.PropertyContainer;
import ru.timeconqueror.timecore.internal.common.packet.CoffeeCapabilityDataPacket;
import ru.timeconqueror.timecore.internal.common.packet.InternalPacketManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public abstract class CoffeeCapabilityInstance<T extends ICapabilityProvider> extends PropertyContainer implements INBTSerializable<CompoundTag> {
    private final CapabilityOwnerType<? super T> ownerType;

    public abstract Capability<? extends CoffeeCapabilityInstance<T>> getCapability();

    /**
     * Checks if properties of capability have changed and if yes, sends them.
     * Works in both directions.
     */
    public void detectAndSendChanges(Level level, T owner) {
        sendData(level, owner, prop -> {
            if (prop.getChanged()) {
                prop.setChanged(false);
                return true;
            }
            return false;
        });
    }

    /**
     * Synchronizes all capability data.
     * Works in both directions.
     */
    public void sendAllData(Level level, T owner) {
        sendData(level, owner, prop -> true);
    }

    public abstract void sendChangesToClient(SimpleChannel channel, Object message);

    private void sendData(Level level, T owner, Predicate<CoffeeProperty<?>> predicate) {
        boolean clientSide = level.isClientSide();

        Object message = createDataPacket(level, owner, clientSide, predicate);

        if (message != null) {
            if (clientSide) {
                InternalPacketManager.INSTANCE.sendToServer(message);
            } else {
                sendChangesToClient(InternalPacketManager.INSTANCE, message);
            }
        }
    }

    private Object createDataPacket(Level level, T owner, boolean clientSide, Predicate<CoffeeProperty<?>> syncPredicate) {
        return CoffeeCapabilityDataPacket.Companion.create(level, owner, this, clientSide, syncPredicate);
    }

    /**
     * Marks owner to save its data as well as this capability
     */
    public void markForSave(T owner) {
        ownerType.save(owner);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        serialize(prop -> true, compound, false, SerializationType.SAVE);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        deserialize(nbt);
    }
}