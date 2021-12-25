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
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer;

import java.util.ArrayList;

public class CapabilityOwner<T extends ICapabilityProvider> {

    public static final CapabilityOwner<BlockEntity> TILE_ENTITY = new CapabilityOwner<>(new CapabilityOwnerSerializer<BlockEntity>() {
        @Override
        public void serializeOwner(Level world, BlockEntity owner, CompoundTag nbt) {
            nbt.putInt("x", owner.getBlockPos().getX());
            nbt.putInt("y", owner.getBlockPos().getY());
            nbt.putInt("z", owner.getBlockPos().getZ());
        }

        @Override
        public BlockEntity deserializeOwner(Level world, CompoundTag nbt) {
            BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
            return world.getBlockEntity(pos);
        }
    });

    public static final CapabilityOwner<Entity> ENTITY = new CapabilityOwner<>(new CapabilityOwnerSerializer<Entity>() {
        @Override
        public void serializeOwner(Level world, Entity owner, CompoundTag nbt) {
            nbt.putInt("id", owner.getId());
        }//FIXME not uuid?

        @Override
        public Entity deserializeOwner(Level world, CompoundTag nbt) {
            return world.getEntity(nbt.getInt("id"));
        }
    });

    public static final CapabilityOwner<Level> WORLD = new CapabilityOwner<>(new CapabilityOwnerSerializer<Level>() {

        @Override
        public void serializeOwner(Level world, Level owner, CompoundTag nbt) {
        }

        @Override
        public Level deserializeOwner(Level world, CompoundTag nbt) {
            return world;
        }

    });

    public static final CapabilityOwner<LevelChunk> CHUNK = new CapabilityOwner<>(new CapabilityOwnerSerializer<LevelChunk>() {
        @Override
        public void serializeOwner(Level world, LevelChunk owner, CompoundTag nbt) {
            nbt.putInt("x", owner.getPos().x);
            nbt.putInt("z", owner.getPos().z);
        }

        @Override
        public LevelChunk deserializeOwner(Level world, CompoundTag nbt) {
            return world.getChunk(nbt.getInt("x"), nbt.getInt("z"));
        }
    });

    public static final CapabilityOwner<ItemStack> ITEM_STACK = new CapabilityOwner<>(null);

    // TODO: Village capability
//    public static final CapabilityOwner<VillagePieces.Village> VILLAGE = new CapabilityOwner<>(new CapabilityOwnerSerializer<VillagePieces.Village>() {
//        @Override
//        public void serializeOwner(World world, VillagePieces.Village owner, CompoundNBT nbt) {
//            nbt.putInt("x", owner.getCenter().getX());
//            nbt.putInt("y", owner.getCenter().getY());
//            nbt.putInt("z", owner.getCenter().getZ());
//        }
//
//        @Override
//        public VillagePieces.Village deserializeOwner(World world, CompoundNBT nbt) {
//            BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
//
//            for (VillagePieces.Village village : world.getVillageCollection().getVillageList()) {
//                if (village.getCenter().equals(pos)) {
//                    return village;
//                }
//            }
//
//            return null;
//        }
//    });

    private final ArrayList<CoffeeCapabilityAttacher<T, ?>> attachers = new ArrayList<>();
    private final CapabilityOwnerSerializer<T> serializer;

    public CapabilityOwner(CapabilityOwnerSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public ArrayList<CoffeeCapabilityAttacher<T, ?>> getAttachers() {
        return attachers;
    }

    public CapabilityOwnerSerializer<T> getSerializer() {
        return serializer;
    }
}
