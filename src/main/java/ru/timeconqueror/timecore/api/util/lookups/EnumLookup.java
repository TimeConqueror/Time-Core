package ru.timeconqueror.timecore.api.util.lookups;

import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface EnumLookup<E extends Enum<E>, S> {
    E by(S id);

    S from(E value);

    static <T extends Enum<T>, S> EnumLookup<T, S> make(Class<T> enumClass, Function<T, S> idSupplier) {
        T[] enumValues = ReflectionHelper.getEnumValues(enumClass);

        Map<S, T> idLookup = new HashMap<>(enumValues.length);

        for (T val : enumValues) {
            if (idLookup.put(idSupplier.apply(val), val) != null) {
                throw new IllegalArgumentException("Found duplication of id " + idSupplier.apply(val) + " for " + enumClass.getName());
            }
        }

        return new MapBasedLookup<>(enumClass, idLookup, idSupplier);
    }

    static <T extends Enum<T>> IndexBasedLookup<T> makeFromOrdinal(Class<T> enumClass) {
        return new IndexBasedLookup<>(enumClass);
    }

    class MapBasedLookup<E extends Enum<E>, S> implements EnumLookup<E, S> {
        private final Class<E> clazz;
        private final Map<S, E> lookup;
        private final Function<E, S> toIdFunc;

        private MapBasedLookup(Class<E> clazz, Map<S, E> lookup, Function<E, S> toIdFunc) {
            this.clazz = clazz;
            this.lookup = lookup;
            this.toIdFunc = toIdFunc;
        }

        @Override
        public E by(S id) {
            if (!lookup.containsKey(id)) {
                throw new IllegalArgumentException("Instance of enum " + clazz.getSimpleName() + " was not found for id '" + id + "'");
            }

            return lookup.get(id);
        }

        @Override
        public S from(E value) {
            return toIdFunc.apply(value);
        }
    }

    class IndexBasedLookup<E extends Enum<E>> implements EnumLookup<E, Integer> {
        private final Class<E> clazz;
        private final E[] values;

        private IndexBasedLookup(Class<E> clazz) {
            this.clazz = clazz;
            this.values = ReflectionHelper.getEnumValues(clazz);
        }

        @Override
        public E by(Integer id) {
            if (id < 0 || id >= values.length) {
                throw new IllegalArgumentException("There's no instance " + clazz.getSimpleName() + " with index '" + id + "'");
            }
            return values[id];
        }

        @Override
        public Integer from(E value) {
            return value.ordinal();
        }
    }
}
