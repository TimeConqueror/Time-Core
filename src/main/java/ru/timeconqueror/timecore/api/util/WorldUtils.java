package ru.timeconqueror.timecore.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;

public class WorldUtils {
    public static <T> void forTypedTile(Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, s -> {
        });
    }

    public static <T> void forTypedTileWithWarn(Player player, Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, message -> {
            NetworkUtils.sendMessage(player, new TextComponent(message).withStyle(ChatFormatting.RED));
            TimeCore.LOGGER.warn(message, new IllegalAccessException());
        });
    }

    public static <T> void forTypedTileWithWarn(Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, message -> TimeCore.LOGGER.warn(message, new IllegalAccessException()));
    }

    public static <T> void forTileWithReqt(Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, error -> {
            throw new IllegalStateException(error);
        });
    }

    public static <T> void forTypedTile(Level world, BlockPos pos, Class<T> clazz, Consumer<T> action, Consumer<String> errorHandler) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile == null) {
            errorHandler.accept("Error. There's no tile on " + pos);//TODO localize here and in LootGames, TODO "more info in logs, where will be current block"
            return;
        }

        if (clazz.isInstance(tile)) {
            action.accept((T) tile);
        } else {
            errorHandler.accept("Error. There's a tile " + tile.getClass().getName() + " instead of " + clazz.getName() + " on " + pos);
        }
    }
}
