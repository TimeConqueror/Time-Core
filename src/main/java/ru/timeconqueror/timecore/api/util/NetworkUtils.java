package ru.timeconqueror.timecore.api.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;

public class NetworkUtils {
    /**
     * Returns all players that are in specific distance from given pos.
     */
    public static List<ServerPlayerEntity> getPlayersNearby(BlockPos fromPos, double distanceIn) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        return players.stream().filter(player -> {
            double distanceSq = player.getDistanceSq(fromPos.getX(), fromPos.getY(), fromPos.getZ());
            return distanceIn * distanceIn <= distanceSq;
        }).collect(Collectors.toList());
    }
}
