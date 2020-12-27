package ru.timeconqueror.timecore.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ru.timeconqueror.timecore.api.util.reflection.ReflectionHelper;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ExtraCodecs {
    public static Codec<Block> BLOCK = ResourceLocation.CODEC.comapFlatMap(location -> throwOnNull(location, ForgeRegistries.BLOCKS::getValue, Block.class), ForgeRegistryEntry::getRegistryName);

    public static Codec<Direction> DIRECTION = Codec.STRING.comapFlatMap(name -> throwOnNull(name, Direction::byName, Direction.class), Direction::getName);
    public static Codec<Direction> HORIZONTAL_DIRECTION = Codec.INT.xmap(Direction::from2DDataValue, Direction::get2DDataValue);

    public static <T extends Enum<T>, S> Codec<T> forEnum(Class<T> enumClass, Function<T, S> idSupplier, Codec<S> idCodec) {
        T[] enumValues = ReflectionHelper.getEnumValues(enumClass);

        Map<S, T> idLookup = new HashMap<>(enumValues.length);

        for (T val : enumValues) {
            if (idLookup.put(idSupplier.apply(val), val) != null) {
                throw new IllegalArgumentException("Found duplication of id " + idSupplier.apply(val) + " for " + enumClass.getName());
            }
        }

        return idCodec.xmap(idLookup::get, idSupplier);
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

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Codec<T> forEnumByOrdinal(Class<T> enumClass) {
        T[] enumValues = ReflectionHelper.getEnumValues(enumClass);

        T[] idLookup = (T[]) Array.newInstance(enumClass, enumValues.length);

        for (T val : enumValues) {
            idLookup[val.ordinal()] = val;
        }

        return Codec.INT.xmap(id -> idLookup[id], Enum::ordinal);
    }
}