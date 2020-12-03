package ru.timeconqueror.timecore.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ExtraCodecs {
    public static Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if (block != null) {
            return DataResult.success(block);
        } else {
            return DataResult.error("Unknown block with '" + location + "' key");
        }
    }, ForgeRegistryEntry::getRegistryName);

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