package ru.timeconqueror.timecore.api.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Empty {
    private static final Runnable EMPTY_RUNNABLE = () -> {
    };
    private static final Consumer<?> EMPTY_CONSUMER = o -> {
    };

    public static Runnable runnable() {
        return EMPTY_RUNNABLE;
    }

    public static <T> Consumer<T> consumer() {
        return (Consumer<T>) EMPTY_CONSUMER;
    }

    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    public static <T> Set<T> set() {
        return Collections.emptySet();
    }

    public static <K, V> Map<K, V> map() {
        return Collections.emptyMap();
    }
}
