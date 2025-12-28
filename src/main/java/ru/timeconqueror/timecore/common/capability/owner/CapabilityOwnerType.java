package ru.timeconqueror.timecore.common.capability.owner;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;

import java.util.ArrayList;

public class CapabilityOwnerType<T extends ICapabilityProvider> {

    public static final CapabilityOwnerType<BlockEntity> BLOCK_ENTITY = new CapabilityOwnerType<>(new CapabilityOwnerCodec<>() {
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
    }, BlockEntity::setChanged);

    public static final CapabilityOwnerType<Entity> ENTITY = new CapabilityOwnerType<>(new CapabilityOwnerCodec<>() {
        @Override
        public void serialize(Level world, Entity owner, CompoundTag nbt) {
            nbt.putInt("id", owner.getId());
        }

        @Override
        public Entity deserialize(Level world, CompoundTag nbt) {
            return world.getEntity(nbt.getInt("id"));
        }
    }, owner -> {
        //TODO should really be empty?
    });

    public static final CapabilityOwnerType<Level> LEVEL = new CapabilityOwnerType<>(new CapabilityOwnerCodec<>() {

        @Override
        public void serialize(Level world, Level owner, CompoundTag nbt) {
        }

        @Override
        public Level deserialize(Level world, CompoundTag nbt) {
            return world;
        }

    }, owner -> {
        //TODO should really be empty?
    });

    public static final CapabilityOwnerType<LevelChunk> CHUNK = new CapabilityOwnerType<>(new CapabilityOwnerCodec<>() {
        @Override
        public void serialize(Level world, LevelChunk owner, CompoundTag nbt) {
            nbt.putInt("x", owner.getPos().x);
            nbt.putInt("z", owner.getPos().z);
        }

        @Override
        public LevelChunk deserialize(Level world, CompoundTag nbt) {
            return world.getChunk(nbt.getInt("x"), nbt.getInt("z"));
        }
    }, owner -> owner.setUnsaved(true));

    public static final CapabilityOwnerType<ItemStack> ITEM_STACK = new CapabilityOwnerType<>(null, owner -> {
    });

    @Getter
    private final ArrayList<CoffeeCapabilityAttacher<T, ?>> attachers = new ArrayList<>();
    @Getter
    private final CapabilityOwnerCodec<T> serializer;
    private final SaveFunc<T> saveFunc;

    public CapabilityOwnerType(CapabilityOwnerCodec<T> serializer, @Nullable SaveFunc<T> saveFunc) {
        this.serializer = serializer;
        this.saveFunc = saveFunc;
    }

    public void save(T owner) {
        if (saveFunc != null) {
            saveFunc.save(owner);
        }
    }

    public interface SaveFunc<T> {
        void save(T owner);
    }
}
