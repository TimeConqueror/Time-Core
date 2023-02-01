package ru.timeconqueror.timecore.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.api.util.holder.Pair;

import java.util.Arrays;

public class ChatUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Sends message to all players in given distance.
     *
     * @param distanceIn distance from `fromPos`, in which players will be get a message.
     */
    public static void sendToAllNearby(BlockPos fromPos, Component msg, double distanceIn) {
        for (ServerPlayer player : NetworkUtils.getPlayersNearby(fromPos, distanceIn)) {
            //FIXME port
           // player.sendMessage(msg, player.getUUID());
        }
    }

    /**
     * Changes format (usually color) of provided message.
     */
    public static MutableComponent format(MutableComponent msg, ChatFormatting... formats) {
        return msg.withStyle(formats);
    }

    /**
     * Sends an error message to the player and prints extra info to the log file.
     *
     * @param player    player to send the message
     * @param msg       message to send
     * @param extraInfo extra info that is present as the "name->value" pair array
     */
    @SafeVarargs
    public static void sendInformativeError(String modId, Player player, String msg, Pair<String, Object>... extraInfo) {
        sendInformativeError(modId, player, msg, new Exception(""));
    }

    /**
     * Sends an error message to the player and prints extra info to the log file.
     *
     * @param player    player to send the message
     * @param msg       message to send
     * @param exception exception that will be printed to logs.
     * @param extraInfo extra info that is present as the "name->value" pair array
     */
    @SafeVarargs
    public static void sendInformativeError(String modId, Player player, String msg, Exception exception, Pair<String, Object>... extraInfo) {
        NetworkUtils.sendMessage(player, Component.translatable("msg.timecore.chat_error_header").withStyle(ChatFormatting.RED).append(msg));
        NetworkUtils.sendMessage(player, Component.translatable("msg.timecore.chat_error_contact_us").withStyle(ChatFormatting.RED).append(modId));
        LOGGER.error(msg);
        LOGGER.error("Extra information: " + Arrays.toString(extraInfo));
        LOGGER.error(exception);
    }
}
