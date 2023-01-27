package ru.timeconqueror.timecore.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.util.lookups.EnumLookup;

import java.util.function.Function;

public class ExtraCodecs {
    public static Codec<Block> BLOCK = ResourceLocation.CODEC.comapFlatMap(location -> throwOnNull(location, ForgeRegistries.BLOCKS::getValue, Block.class), ForgeRegistries.BLOCKS::getKey);

    public static Codec<Direction> DIRECTION = Codec.STRING.comapFlatMap(name -> throwOnNull(name, Direction::byName, Direction.class), Direction::getName);
    public static Codec<Direction> HORIZONTAL_DIRECTION = Codec.INT.xmap(Direction::from2DDataValue, Direction::get2DDataValue);

    public static <T extends Enum<T>, S> Codec<T> forEnum(Class<T> enumClass, Function<T, S> idSupplier, Codec<S> idCodec) {
        return idCodec.xmap(EnumLookup.make(enumClass, idSupplier)::by, idSupplier);
    }

    /**
     * Can be used for proper deserializing values from serialized key.
     * What if function, which deserializes values from keys return null?
     * It's better to handle such situations, so here is a method to do this.
     *
     * @param key          key from which value will be deserialized
     * @param deserializer function, which deserializes value from key, can return null, which will result in exception.
     * @param clazz        value class
     */
    public static <S, R> DataResult<R> throwOnNull(S key, Function<S, R> deserializer, Class<R> clazz) {
        R value = deserializer.apply(key);
        if (value != null) {
            return DataResult.success(value);
        } else {
            return DataResult.error("Unknown " + clazz.getSimpleName() + " with '" + key + "' key");
        }
    }

    public static <T extends Enum<T>> Codec<T> forEnumByOrdinal(Class<T> enumClass) {
        T[] values = ReflectionHelper.getEnumValues(enumClass);
        return Codec.INT.xmap(ordinal -> values[ordinal], Enum::ordinal);
    }
}