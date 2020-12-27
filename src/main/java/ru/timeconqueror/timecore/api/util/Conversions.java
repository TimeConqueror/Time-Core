package ru.timeconqueror.timecore.api.util;

import java.util.Iterator;
import java.util.function.Function;

public class Conversions {
    public static <T> String toString(T[] arr, String separator, Function<T, String> mapper) {
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            T v = arr[i];

            b.append(mapper.apply(v));

            if (i != arr.length - 1) {
                b.append(separator).append(' ');
            }
        }

        return b.toString();
    }

    public static <T> String toString(Iterable<T> iterable, String separator, Function<T, String> mapper) {
        StringBuilder b = new StringBuilder();

        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T v = iterator.next();

            b.append(mapper.apply(v));

            if (iterator.hasNext()) {
                b.append(separator).append(' ');
            }
        }

        return b.toString();
    }
}
