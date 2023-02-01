package ru.timeconqueror.timecore.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.timecore.TimeCore;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LevelUtils {
    public static <T> void forTypedTile(Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, s -> {
        });
    }

    public static <T> void forTypedTileWithWarn(Player player, Level world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        forTypedTile(world, pos, clazz, action, message -> {
            NetworkUtils.sendMessage(player, Component.literal(message).withStyle(ChatFormatting.RED));
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

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, BlockEntityType<E> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType == providedType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, Supplier<BlockEntityType<E>> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType.get() == providedType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static Explosion explode(Level level, @Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, Explosion.BlockInteraction modeIn) {
        return explode(level, entityIn, null, null, xIn, yIn, zIn, explosionRadius, false, modeIn);
    }

    public static Explosion explode(Level level, @Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, boolean causesFire, Explosion.BlockInteraction modeIn) {
        return explode(level, entityIn, null, null, xIn, yIn, zIn, explosionRadius, causesFire, modeIn);
    }

    public static Explosion explode(Level level, @Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode) {
        Explosion explosion = new Explosion(level, exploder, damageSource, damageCalculator, x, y, z, size, causesFire, mode);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;
        explosion.explode();
        explosion.finalizeExplosion(true);
        return explosion;
    }
}
