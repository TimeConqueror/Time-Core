package ru.timeconqueror.timecore.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class ChatUtils {
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
    public static <T extends ITextComponent> T format(T msg, TextFormatting format) {
        msg.getStyle().withColor(format);
        return msg;
    }
}
