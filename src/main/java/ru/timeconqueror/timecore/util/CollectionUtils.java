package ru.timeconqueror.timecore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CollectionUtils {
    public static <T> boolean contains(T[] array, T object) {
        for (T t : array) {
            if (t.equals(object)) return true;
        }

        return false;
    }

    public static <T> boolean contains(Iterable<T> list, T object) {
        for (T t : list) {
            if (t.equals(object)) return true;
        }

        return false;
    }

    /**
     * Maps array to list by provided mapper function.
     *
     * @param source source array, from which we will map elements
     * @param mapper function, which handles the conversion of every element in source array.
     * @param <T>    type in
     * @param <R>    type out
     */
    public static <T, R> List<R> mapToList(T[] source, Function<T, R> mapper) {
        List<R> list = new ArrayList<>(source.length);

        for (T t : source) {
            list.add(mapper.apply(t));
        }

        return list;
    }

    /**
     * Maps array to another array by provided mapper function.
     *
     * @param source           source array, from which we will map elements
     * @param mappedArrFactory factory, which accepts new array length and should provide new array.
     * @param mapper           function, which handles the conversion of every element in source array.
     * @param <T>              type in
     * @param <R>              type out
     */
    public static <T, R> R[] map(T[] source, Function<Integer, R[]> mappedArrFactory, Function<T, R> mapper) {
        R[] mapped = mappedArrFactory.apply(source.length);

        Requirements.arrayWithLength(mapped, source.length, "Mapped array should have the same length (" + source.length + ") as source array. Provided length:" + mapped.length);

        for (int i = 0; i < source.length; i++) {
            mapped[i] = mapper.apply(source[i]);
        }

        return mapped;
    }
}
