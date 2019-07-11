package ru.timeconqueror.timecore.util;

public class CastHelper {
    public static <T1, T2 extends T1> void cast(T1[] source, T2[] target) throws ClassCastException {
        if (target.length != source.length) {
            throw new ArrayIndexOutOfBoundsException("Length of source and target massive must be equal for proper copying");
        }

        for (int i = 0; i < target.length; i++) {
            target[i] = (T2) source[i];
        }
    }
}
