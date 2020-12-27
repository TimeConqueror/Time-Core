package ru.timeconqueror.timecore.api.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetworkUtils {
    /**
     * Returns all players who are in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static List<ServerPlayerEntity> getPlayersNearby(BlockPos fromPos, double distanceIn) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        return players.stream().filter(player -> {
            double distanceSq = player.distanceToSqr(fromPos.getX(), fromPos.getY(), fromPos.getZ());
            return distanceIn * distanceIn <= distanceSq;
        }).collect(Collectors.toList());
    }

    /**
     * Do provided action for each player, who is in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static void forEachPlayerNearby(BlockPos fromPos, double distanceIn, Consumer<ServerPlayerEntity> action) {
        for (ServerPlayerEntity player : getPlayersNearby(fromPos, distanceIn)) {
            action.accept(player);
        }
    }

    /**
     * Send provided message for each player, who is in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static void sendForEachPlayerNearby(BlockPos fromPos, double distanceIn, ITextComponent component) {
        forEachPlayerNearby(fromPos, distanceIn, serverPlayerEntity -> serverPlayerEntity.sendMessage(component, serverPlayerEntity.getUUID()));
    }

    /**
     * Send provided message for provided player.
     */
    public static void sendMessage(PlayerEntity player, ITextComponent component) {
        player.sendMessage(component, player.getUUID());
    }

    /**
     * Do provided action for each player, who is tracking this block position.
     */
    public static void forAllTracking(ServerWorld world, BlockPos fromPos, Consumer<ServerPlayerEntity> action) {
        ChunkPos chunkPos = new ChunkPos(fromPos);
        world.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(action);
    }

    /**
     * Find player by its uuid.
     *
     * @apiNote Only for logical server side!
     */
    public static Optional<ServerPlayerEntity> getPlayer(UUID uuid) {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid));
    }
}
