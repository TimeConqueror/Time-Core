package ru.timeconqueror.timecore.api.util;

public class SimpleName {
    public static String of(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
