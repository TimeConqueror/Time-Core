package ru.timeconqueror.timecore.animation.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.common.registry.LevelObjectCodecs;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CAnimationPacket;

public class BlockEntityNetworkDispatcher<T extends BlockEntity & AnimatedObject<T>> extends NetworkDispatcher<T> {
    @Override
    public void send(T boundObject, S2CAnimationPacket packet) {
        Level level = boundObject.getLevel();
        LevelChunk chunk = level.getChunkAt(boundObject.getBlockPos());
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(boundObject.getBlockPos()), packet);
    }

    @Override
    public LevelObjectCodec<BlockEntity> getCodec(T boundObject) {
        return LevelObjectCodecs.BLOCK_ENTITY.create(boundObject);
    }
}
