package ru.timeconqueror.timecore.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public static <T> boolean allMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (!predicate.test(obj)) return false;
        }
        return true;
    }

    public static <T> boolean anyMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (predicate.test(obj)) return true;
        }
        return false;
    }

    public static <T> boolean noneMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (predicate.test(obj)) return false;
        }
        return true;
    }

    /**
     * Maps array to list by provided mapper function.
     *
     * @param source source array, from which we will map elements
     * @param mapper function, which handles the conversion of every element in source array.
     * @param <T>    type in
     * @param <R>    type out
     */
    public static <T, R> List<R> mapArrayToList(T[] source, Function<T, R> mapper) {
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
    public static <T, R> R[] mapArray(T[] source, Function<Integer, R[]> mappedArrFactory, Function<T, R> mapper) {
        R[] mapped = mappedArrFactory.apply(source.length);

        Requirements.arrayWithLength(mapped, source.length, "Mapped array should have the same length (" + source.length + ") as source array. Provided length:" + mapped.length);

        for (int i = 0; i < source.length; i++) {
            mapped[i] = mapper.apply(source[i]);
        }

        return mapped;
    }
}
