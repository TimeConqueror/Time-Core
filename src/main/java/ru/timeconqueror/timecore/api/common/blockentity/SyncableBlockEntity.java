package ru.timeconqueror.timecore.api.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

/**
 * Syncable BlockEntity.
 * <p>
 * You can also control what should be sent to client
 * and what should be only used for saving.
 */
public abstract class SyncableBlockEntity extends SimpleBlockEntity {
    public SyncableBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * For saving/sending data use {@link #writeNBT(CompoundTag, SerializationType, HolderLookup.Provider)}
     */
    @NotNull
    @Override
    public final void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        writeNBT(compound, SerializationType.SAVE, provider);
    }

    /**
     * For saving/sending data use {@link #readNBT(CompoundTag, SerializationType, HolderLookup.Provider)}
     */
    @Override
    public final void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        //If read from client side
        if (compound.contains("client_flag")) {
            readNBT(compound, SerializationType.SYNC, registries);
        } else {
            readNBT(compound, SerializationType.SAVE, registries);
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void writeNBT(CompoundTag nbt, SerializationType type, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
    }

    @OverridingMethodsMustInvokeSuper
    protected void readNBT(CompoundTag nbt, SerializationType type, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
    }

    @Nonnull
    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compound = new CompoundTag();

        writeNBT(compound, SerializationType.SYNC, registries);

        compound.putByte("client_flag", (byte) 0);
        return compound;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag compound = pkt.getTag();

        readNBT(compound, SerializationType.SYNC, registries);
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Saves current data to the disk and sends update to client.
     */
    public void saveAndSync() {
        if (isServerSide()) {
            setBlockToUpdateAndSave();
        }
    }

    /**
     * Saves current data to the disk without sending update to client.
     */
    public void save() {
        if (isServerSide()) {
            setChanged();
        }
    }

    /**
     * Saves the block to disk, sends packet to update it on client.
     */
    private void setBlockToUpdateAndSave() {
        Objects.requireNonNull(level);

        setChanged();
        level.sendBlockUpdated(worldPosition, getState(), getState(), 2);
    }
}
