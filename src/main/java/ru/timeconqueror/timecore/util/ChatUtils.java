package ru.timeconqueror.timecore.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ChatUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Sends message to all players in given distance.
     *
     * @param distanceIn distance from `fromPos`, in which players will be get a message.
     */
    public static void sendMessageToAllNearby(BlockPos fromPos, ITextComponent msg, double distanceIn) {
        for (ServerPlayerEntity player : NetworkUtils.getPlayersNearby(fromPos, distanceIn)) {
            player.sendMessage(msg, player.getUUID());
        }
    }

    /**
     * Changes format (usually color) of provided message.
     */
    public static IFormattableTextComponent format(IFormattableTextComponent msg, TextFormatting... formats) {
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
    public static void sendInformativeError(String modId, PlayerEntity player, String msg, Pair<String, Object>... extraInfo) {
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
    public static void sendInformativeError(String modId, PlayerEntity player, String msg, Exception exception, Pair<String, Object>... extraInfo) {
        NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.timecore.chat_error_header").withStyle(TextFormatting.RED).append(msg));
        NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.timecore.chat_error_contact_us").withStyle(TextFormatting.RED).append(modId));
        LOGGER.error(msg);
        LOGGER.error("Extra information: " + Arrays.toString(extraInfo));
        LOGGER.error(exception);
    }
}
