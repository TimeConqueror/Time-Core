package ru.timeconqueror.timecore.api.reflection.provider;

import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedMethod;

import java.lang.reflect.Method;

public class JavaClassHandler implements ClassHandler {

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }

    @Override
    public <R> boolean isStatic(UnlockedMethod<R> method) {
        return method.isStatic();
    }

    @Override
    public <R> void requireStatic(UnlockedMethod<R> method) {
        if (!isStatic(method)) {
            throw new IllegalArgumentException("Method " + method + " should be static!");
        }
    }

    @Override
    public <R> UnlockedMethod<R> findMethod(Class<?> clazz, String signature) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (ReflectionHelper.getMethodSignature(declaredMethod).equals(signature)) {
                return new UnlockedMethod<>(declaredMethod);
            }
        }
        return null;
    }

    @Override
    public <R> Object invokeStaticMethod(UnlockedMethod<R> method, Object... args) {
        return method.invoke(null, args);
    }
}
