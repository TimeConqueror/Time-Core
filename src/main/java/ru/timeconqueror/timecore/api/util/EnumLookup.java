package ru.timeconqueror.timecore.api.util;

import ru.timeconqueror.timecore.api.util.reflection.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EnumLookup<E extends Enum<E>, S> {
    private final Class<E> clazz;
    private final Map<S, E> lookup;

    private EnumLookup(Class<E> clazz, Map<S, E> lookup) {
        this.clazz = clazz;
        this.lookup = lookup;
    }

    public static <T extends Enum<T>, S> EnumLookup<T, S> make(Class<T> enumClass, Function<T, S> idSupplier) {
        T[] enumValues = ReflectionHelper.getEnumValues(enumClass);

        Map<S, T> idLookup = new HashMap<>(enumValues.length);

        for (T val : enumValues) {
            if (idLookup.put(idSupplier.apply(val), val) != null) {
                throw new IllegalArgumentException("Found duplication of id " + idSupplier.apply(val) + " for " + enumClass.getName());
            }
        }

        return new EnumLookup<>(enumClass, idLookup);
    }

    public E get(S id) {
        if (!lookup.containsKey(id)) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " instance was not found for id '" + id + "'");
        }

        return lookup.get(id);
    }
}
