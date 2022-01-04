package ru.timeconqueror.timecore.api.reflection;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper for method, tries to unlock the access to it.
 *
 * @param <O> owner type.
 * @param <T> method return type.
 */
public class UnlockedMethod<O, T> {
    private final Method method;
    private final boolean accessible;
    private final boolean isStatic;

    public UnlockedMethod(Method method) {
        this.method = method;

        accessible = method.trySetAccessible();
        isStatic = ReflectionHelper.isStatic(method);
    }

    /**
     * Invokes method with given params.
     *
     * @param methodOwner owner of method. If the underlying method is static, the methodOwner argument is ignored; it may be null.
     * @param params      the parameters used for the method call.
     * @return object return by method or null if the method has {@code void} modifier.
     */
    @SuppressWarnings("unchecked")
    public T invoke(@Nullable O methodOwner, Object... params) {
        if (!accessible) {
            throw new UnsupportedOperationException("The method isn't accessible");
        }

        if (!isStatic && methodOwner == null) {
            throw new IllegalArgumentException(String.format("Tried to pass null as a methodOwner to the non static method %s", method.toString()));
        }

        try {
            return (T) method.invoke(methodOwner, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Method unboxed() {
        return method;
    }

    /**
     * Returns true, if provided method is static, otherwise returns false.
     */
    public boolean isStatic() {
        return isStatic;
    }

    public boolean guessIsAccessible() {
        return accessible;
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
