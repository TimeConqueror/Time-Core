package ru.timeconqueror.timecore.common.capability.owner;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer;

import java.util.ArrayList;

public class CapabilityOwner<T extends ICapabilityProvider> {

    public static final CapabilityOwner<TileEntity> TILE_ENTITY = new CapabilityOwner<>(new CapabilityOwnerSerializer<TileEntity>() {
        @Override
        public void serializeOwner(World world, TileEntity owner, CompoundNBT nbt) {
            nbt.putInt("x", owner.getBlockPos().getX());
            nbt.putInt("y", owner.getBlockPos().getY());
            nbt.putInt("z", owner.getBlockPos().getZ());
        }

        @Override
        public TileEntity deserializeOwner(World world, CompoundNBT nbt) {
            BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
            return world.getBlockEntity(pos);
        }
    });

    public static final CapabilityOwner<Entity> ENTITY = new CapabilityOwner<>(new CapabilityOwnerSerializer<Entity>() {
        @Override
        public void serializeOwner(World world, Entity owner, CompoundNBT nbt) {
            nbt.putInt("id", owner.getId());
        }

        @Override
        public Entity deserializeOwner(World world, CompoundNBT nbt) {
            return world.getEntity(nbt.getInt("id"));
        }
    });

    public static final CapabilityOwner<World> WORLD = new CapabilityOwner<>(new CapabilityOwnerSerializer<World>() {

        @Override
        public void serializeOwner(World world, World owner, CompoundNBT nbt) {
        }

        @Override
        public World deserializeOwner(World world, CompoundNBT nbt) {
            return world;
        }

    });

    public static final CapabilityOwner<Chunk> CHUNK = new CapabilityOwner<>(new CapabilityOwnerSerializer<Chunk>() {
        @Override
        public void serializeOwner(World world, Chunk owner, CompoundNBT nbt) {
            nbt.putInt("x", owner.getPos().x);
            nbt.putInt("z", owner.getPos().z);
        }

        @Override
        public Chunk deserializeOwner(World world, CompoundNBT nbt) {
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
