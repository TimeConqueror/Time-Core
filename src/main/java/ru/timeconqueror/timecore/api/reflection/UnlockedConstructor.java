package ru.timeconqueror.timecore.api.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper for constructor, tries to unlock the access to it.
 *
 * @param <T> type of instances that are created by this constructor.
 */
public class UnlockedConstructor<T> {
    private final Constructor<T> c;
    private final boolean accessible;

    public UnlockedConstructor(Constructor<T> c) {
        this.c = c;
        accessible = c.trySetAccessible();
    }

    /**
     * Creates a new instance.
     * Safe for use with non-accessible constructors.
     *
     * @param initParams an array of objects to be passed as parameters to the constructor call.
     * @return new instance.
     */
    public T newInstance(Object... initParams) {
        if (!accessible) {
            throw new UnsupportedOperationException("The constructor isn't accessible");
        }

        try {
            return c.newInstance(initParams);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Constructor<T> unboxed() {
        return c;
    }

    @Override
    public String toString() {
        return c.toString();
    }
}
