package ru.timeconqueror.timecore.util;

import com.mojang.serialization.Codec;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ExtraCodecs {
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
}