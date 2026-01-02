package ru.timeconqueror.timecore.mixins;

import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.internal.common.TimeEventHooks;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap {
//    @Inject(method = "playerLoadedChunk",
//            at = @At(value = "INVOKE",
//                    shift = At.Shift.AFTER,
//                    target = "Lnet/minecraft/server/level/ServerPlayer;trackChunk(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/network/protocol/Packet;)V")
//    )
//    public void onPlayerLoadedChunk(ServerPlayer player_, MutableObject<ClientboundLevelChunkWithLightPacket> cachedDataPacket_, LevelChunk chunk_, CallbackInfo ci) {
//        TimeEventHooks.onChunkTrackingStart(player_, chunk_);
//    }
}
