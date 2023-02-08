package ru.timeconqueror.timecore.common.capability.owner;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;

import java.util.ArrayList;

public class CapabilityOwner<T extends ICapabilityProvider> {

    public static final CapabilityOwner<BlockEntity> BLOCK_ENTITY = new CapabilityOwner<>(new CapabilityOwnerCodec<>() {
        @Override
        public void serialize(Level world, BlockEntity owner, CompoundTag nbt) {
            nbt.putInt("x", owner.getBlockPos().getX());
            nbt.putInt("y", owner.getBlockPos().getY());
            nbt.putInt("z", owner.getBlockPos().getZ());
        }

        @Override
        public BlockEntity deserialize(Level world, CompoundTag nbt) {
            BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
            return world.getBlockEntity(pos);
        }
    });

    public static final CapabilityOwner<Entity> ENTITY = new CapabilityOwner<>(new CapabilityOwnerCodec<>() {
        @Override
        public void serialize(Level world, Entity owner, CompoundTag nbt) {
            nbt.putInt("id", owner.getId());
        }

        @Override
        public Entity deserialize(Level world, CompoundTag nbt) {
            return world.getEntity(nbt.getInt("id"));
        }
    });

    public static final CapabilityOwner<Level> LEVEL = new CapabilityOwner<>(new CapabilityOwnerCodec<>() {

        @Override
        public void serialize(Level world, Level owner, CompoundTag nbt) {
        }

        @Override
        public Level deserialize(Level world, CompoundTag nbt) {
            return world;
        }

    });

    public static final CapabilityOwner<LevelChunk> CHUNK = new CapabilityOwner<>(new CapabilityOwnerCodec<>() {
        @Override
        public void serialize(Level world, LevelChunk owner, CompoundTag nbt) {
            nbt.putInt("x", owner.getPos().x);
            nbt.putInt("z", owner.getPos().z);
        }

        @Override
        public LevelChunk deserialize(Level world, CompoundTag nbt) {
            return world.getChunk(nbt.getInt("x"), nbt.getInt("z"));
        }
    });

    public static final CapabilityOwner<ItemStack> ITEM_STACK = new CapabilityOwner<>(null);

    private final ArrayList<CoffeeCapabilityAttacher<T, ?>> attachers = new ArrayList<>();
    private final CapabilityOwnerCodec<T> serializer;

    public CapabilityOwner(CapabilityOwnerCodec<T> serializer) {
        this.serializer = serializer;
    }

    public ArrayList<CoffeeCapabilityAttacher<T, ?>> getAttachers() {
        return attachers;
    }

    public CapabilityOwnerCodec<T> getSerializer() {
        return serializer;
    }
}
