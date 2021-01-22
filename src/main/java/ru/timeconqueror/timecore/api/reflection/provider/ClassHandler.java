package ru.timeconqueror.timecore.api.reflection.provider;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.reflection.UnlockedMethod;

public interface ClassHandler {
    boolean canHandle(Class<?> clazz);

    <R> boolean isStatic(UnlockedMethod<R> method);

    <R> void requireStatic(UnlockedMethod<R> method);

    @Nullable <R> UnlockedMethod<R> findMethod(Class<?> clazz, String signature);

    @Nullable <R> Object invokeStaticMethod(UnlockedMethod<R> method, Object... args);
}
