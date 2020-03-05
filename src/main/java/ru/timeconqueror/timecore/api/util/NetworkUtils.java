package ru.timeconqueror.timecore.api.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import ru.timeconqueror.timecore.TimeCore;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetworkUtils {
    /**
     * Returns all players who are in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static List<ServerPlayerEntity> getPlayersNearby(BlockPos fromPos, double distanceIn) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        return players.stream().filter(player -> {
            double distanceSq = player.getDistanceSq(fromPos.getX(), fromPos.getY(), fromPos.getZ());
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
     * Do provided action for each player, who is tracking this block position.
     *
     * @apiNote Only for logical server side!
     */
    public static void forAllTracking(World world, BlockPos fromPos, Consumer<ServerPlayerEntity> action) {
        if (!world.isRemote) {
            ChunkPos chunkPos = new ChunkPos(fromPos);
            ((ServerWorld) world).getChunkProvider().chunkManager.getTrackingPlayers(chunkPos, false).forEach(action);
        } else {
            TimeCore.LOGGER.error("The method #forAllTracking shouldn't be called on the client side.", new IllegalStateException());
        }
    }
}
