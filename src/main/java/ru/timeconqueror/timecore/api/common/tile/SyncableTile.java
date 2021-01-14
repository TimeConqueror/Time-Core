package ru.timeconqueror.timecore.api.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

/**
 * Syncable TileEntity.
 * <p>
 * You can also control what should be sent to client
 * and what should be only used for saving.
 */
public class SyncableTile extends SimpleTile {
    public SyncableTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    /**
     * For saving/sending data use {@link #writeNBT(CompoundNBT, SerializationType)}
     */
    @NotNull
    @Override
    public final CompoundNBT save(CompoundNBT compound) {
        writeNBT(compound, SerializationType.SAVE);

        return compound;
    }

    /**
     * For saving/sending data use {@link #readNBT(BlockState, CompoundNBT, SerializationType)}
     */
    @Override
    public final void load(BlockState state, CompoundNBT compound) {
        //If read from client side
        if (compound.contains("client_flag")) {
            readNBT(state, compound, SerializationType.SYNC);
        } else {
            readNBT(state, compound, SerializationType.SAVE);
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void writeNBT(CompoundNBT nbt, SerializationType type) {
        super.save(nbt);
    }

    @OverridingMethodsMustInvokeSuper
    protected void readNBT(BlockState state, CompoundNBT nbt, SerializationType type) {
        super.load(state, nbt);
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();

        writeNBT(compound, SerializationType.SYNC);

        compound.putByte("client_flag", (byte) 0);
        return compound;
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT compound = pkt.getTag();

        BlockState state = Minecraft.getInstance().level.getBlockState(worldPosition);
        readNBT(state, compound, SerializationType.SYNC);
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, -1 /*useless*/, this.getUpdateTag());
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
