package ru.timeconqueror.timecore.util;

public class CastHelper {
    public static <T1, T2 extends T1> void cast(T1[] source, T2[] target) {
        if (target.length != source.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        try {
            for (int i = 0; i < target.length; i++) {
                target[i] = (T2) source[i];
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
