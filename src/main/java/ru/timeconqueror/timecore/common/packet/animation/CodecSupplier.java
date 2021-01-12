package ru.timeconqueror.timecore.common.packet.animation;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.animation.EnumAnimatedObjectType;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

public abstract class CodecSupplier {
    public static CodecSupplier fromBuffer(PacketBuffer packetBuffer) {
        return EnumAnimatedObjectType.values()[packetBuffer.readInt()].getCodec(packetBuffer);
    }

    public void toBuffer(PacketBuffer buffer) {
        buffer.writeInt(getType().ordinal());
        encode(buffer);
    }

    protected abstract EnumAnimatedObjectType getType();

    protected abstract void encode(PacketBuffer buffer);

    public abstract AnimatedObject<?> construct(World world);

    public static class EntityCodecSupplier extends CodecSupplier {
        private final int id;

        public <T extends Entity & AnimatedObject<T>> EntityCodecSupplier(T entity) {
            this.id = entity.getId();
        }

        public EntityCodecSupplier(PacketBuffer buffer) {
            this.id = buffer.readInt();
        }

        @Override
        protected void encode(PacketBuffer buffer) {
            buffer.writeInt(id);
        }

        @Override
        protected EnumAnimatedObjectType getType() {
            return EnumAnimatedObjectType.ENTITY;
        }

        @Override
        public AnimatedObject<?> construct(World world) {
            return (AnimatedObject<?>) world.getEntity(id);
        }
    }

    public static class TileEntityCodecSupplier extends CodecSupplier {
        private final BlockPos pos;

        public <T extends TileEntity & AnimatedObject<T>> TileEntityCodecSupplier(TileEntity tileEntity) {
            this.pos = tileEntity.getBlockPos();
        }

        public TileEntityCodecSupplier(PacketBuffer buffer) {
            this.pos = buffer.readBlockPos();
        }

        @Override
        protected void encode(PacketBuffer buffer) {
            buffer.writeBlockPos(pos);
        }

        @Override
        public AnimatedObject<?> construct(World world) {
            return (AnimatedObject<?>) world.getBlockEntity(pos);
        }

        @Override
        protected EnumAnimatedObjectType getType() {
            return EnumAnimatedObjectType.TILE_ENTITY;
        }
    }
}
