package ru.timeconqueror.timecore.api.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;

public class WorldUtils {
    public static <T> void forTypedTile(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, s -> {
        });
    }

    public static <T> void forTypedTileWithWarn(PlayerEntity player, World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, message -> {
            NetworkUtils.sendMessage(player, new StringTextComponent(message));
            TimeCore.LOGGER.warn(message, new IllegalAccessException());
        });
    }

    public static <T> void forTypedTileWithWarn(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, message -> TimeCore.LOGGER.warn(message, new IllegalAccessException()));
    }

    public static <T> void forTileWithReqt(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, error -> {
            throw new IllegalStateException(error);
        });
    }

    public static <T> void forTypedTile(World world, BlockPos pos, Class<T> clazz, Consumer<T> action, Consumer<String> errorHandler) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile == null) {
            errorHandler.accept("Can't handle this tile, because it's null on " + pos);
            return;
        }

        if (clazz.isInstance(tile)) {
            action.accept((T) tile);
        } else {
            errorHandler.accept("Can't handle this tile, because it's " + tile.getClass().getName() + " instead of " + clazz.getName() + " on " + pos);
        }
    }
}
