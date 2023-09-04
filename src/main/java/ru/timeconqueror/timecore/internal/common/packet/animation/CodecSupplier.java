package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.animation.AnimatedObjectType;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

public abstract class CodecSupplier {
    public static CodecSupplier fromBuffer(FriendlyByteBuf packetBuffer) {
        return AnimatedObjectType.values()[packetBuffer.readInt()].getCodec(packetBuffer);
    }

    public void toBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(getType().ordinal());
        encode(buffer);
    }

    protected abstract AnimatedObjectType getType();

    protected abstract void encode(FriendlyByteBuf buffer);

    public abstract AnimatedObject<?> construct(Level world);

    public static class EntityCodecSupplier extends CodecSupplier {
        private final int id;

        public <T extends Entity & AnimatedObject<T>> EntityCodecSupplier(T entity) {
            this.id = entity.getId();
        }

        public EntityCodecSupplier(FriendlyByteBuf buffer) {
            this.id = buffer.readInt();
        }

        @Override
        protected void encode(FriendlyByteBuf buffer) {
            buffer.writeInt(id);
        }

        @Override
        protected AnimatedObjectType getType() {
            return AnimatedObjectType.ENTITY;
        }

        @Override
        public AnimatedObject<?> construct(Level world) {
            return (AnimatedObject<?>) world.getEntity(id);
        }
    }

    public static class TileEntityCodecSupplier extends CodecSupplier {
        private final BlockPos pos;

        public <T extends BlockEntity & AnimatedObject<T>> TileEntityCodecSupplier(BlockEntity tileEntity) {
            this.pos = tileEntity.getBlockPos();
        }

        public TileEntityCodecSupplier(FriendlyByteBuf buffer) {
            this.pos = buffer.readBlockPos();
        }

        @Override
        protected void encode(FriendlyByteBuf buffer) {
            buffer.writeBlockPos(pos);
        }

        @Override
        public AnimatedObject<?> construct(Level world) {
            return (AnimatedObject<?>) world.getBlockEntity(pos);
        }

        @Override
        protected AnimatedObjectType getType() {
            return AnimatedObjectType.TILE_ENTITY;
        }
    }
}
