package ru.timeconqueror.timecore.api.util;

import com.google.common.annotations.Beta;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CKickPlayerFromSPPacket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetworkUtils {
    /**
     * Returns all players who are in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static List<ServerPlayer> getPlayersNearby(BlockPos fromPos, double distanceIn) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()
                .stream()
                .filter(player -> {
                    double distanceSq = player.distanceToSqr(fromPos.getX(), fromPos.getY(), fromPos.getZ());
                    return distanceIn * distanceIn >= distanceSq;
                })
                .collect(Collectors.toList());
    }

    /**
     * Do provided action for each player, who is in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static void forEachPlayerNearby(BlockPos fromPos, double distanceIn, Consumer<ServerPlayer> action) {
        for (ServerPlayer player : getPlayersNearby(fromPos, distanceIn)) {
            action.accept(player);
        }
    }

    /**
     * Send provided message for each player, who is in specific distance from given pos.//FIXME check for shouldn't be called on client?
     */
    public static void sendForEachPlayerNearby(BlockPos fromPos, double distanceIn, Component component) {
        forEachPlayerNearby(fromPos, distanceIn, serverPlayerEntity -> serverPlayerEntity.sendMessage(component, serverPlayerEntity.getUUID()));
    }

    /**
     * Send provided message for provided player.
     */
    public static void sendMessage(Player player, Component component) {
        player.sendMessage(component, player.getUUID());
    }

    /**
     * Do provided action for each player, who is tracking this block position.
     */
    public static void forAllTracking(ServerLevel world, BlockPos fromPos, Consumer<ServerPlayer> action) {
        ChunkPos chunkPos = new ChunkPos(fromPos);
        world.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(action);
    }

    /**
     * Find player by its uuid.
     *
     * @apiNote Only for logical server side!
     */
    public static Optional<ServerPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid));
    }

    /**
     * Kicks player from current server.
     * We need this method because if server is integrated (SP) it freezes the client on disconnect.
     */
    @Beta // not tested
    public static void kickPlayer(ServerPlayer player, Component reason) {
        if (EnvironmentUtils.isOnPhysicalClient()) {// if it's an integrated server
            InternalPacketManager.sendToPlayer(player, new S2CKickPlayerFromSPPacket(reason)); //TODO test
        } else {
            player.connection.disconnect(reason);
        }
    }
}
