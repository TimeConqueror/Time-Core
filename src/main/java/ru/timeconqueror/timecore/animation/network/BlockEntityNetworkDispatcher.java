package ru.timeconqueror.timecore.animation.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.common.registry.LevelObjectCodecs;

public class BlockEntityNetworkDispatcher<T extends BlockEntity & AnimatedObject<T>> extends NetworkDispatcher<T> {
    @Override
    public PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
        return PacketDistributor.TRACKING_CHUNK.with(() -> {
            Level world = boundObject.getLevel();
            return world.getChunkAt(boundObject.getBlockPos());
        });
    }

    @Override
    public LevelObjectCodec<BlockEntity> getCodec(T boundObject) {
        return LevelObjectCodecs.BLOCK_ENTITY.create(boundObject);
    }
}
