package ru.timeconqueror.timecore.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.internal.common.packet.animation.CodecSupplier;

public abstract class NetworkDispatcher<T extends AnimatedObject<T>> {
    public static <T extends Entity & AnimatedObject<T>> NetworkDispatcher<T> forEntity() {
        return new NetworkDispatcher<T>() {
            @Override
            protected PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
                return PacketDistributor.TRACKING_ENTITY.with(() -> boundObject);
            }

            @Override
            protected CodecSupplier getCodec(T boundObject) {
                return new CodecSupplier.EntityCodecSupplier(boundObject);
            }
        };
    }

    public static <T extends BlockEntity & AnimatedObject<T>> NetworkDispatcher<T> forTileEntity() {
        return new NetworkDispatcher<>() {
            @Override
            protected PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
                return PacketDistributor.TRACKING_CHUNK.with(() -> {
                    Level world = boundObject.getLevel();
                    return world.getChunkAt(boundObject.getBlockPos());
                });
            }

            @Override
            protected CodecSupplier getCodec(T boundObject) {
                return new CodecSupplier.TileEntityCodecSupplier(boundObject);
            }
        };
    }

    protected abstract PacketDistributor.PacketTarget getPacketTarget(T boundObject);

    protected abstract CodecSupplier getCodec(T boundObject);
}