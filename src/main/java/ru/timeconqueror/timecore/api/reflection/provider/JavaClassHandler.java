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
    public <O, R> boolean isStatic(UnlockedMethod<O, R> method) {
        return method.isStatic();
    }

    @Override
    public <O, R> void requireStatic(UnlockedMethod<O, R> method) {
        if (!isStatic(method)) {
            throw new IllegalArgumentException("Method " + method + " should be static!");
        }
    }

    @Override
    public <O, R> UnlockedMethod<O, R> findMethod(Class<O> clazz, String signature) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (ReflectionHelper.getMethodSignature(declaredMethod).equals(signature)) {
                return new UnlockedMethod<>(declaredMethod);
            }
        }
        return null;
    }

    @Override
    public <O, R> Object invokeStaticMethod(UnlockedMethod<O, R> method, Object... args) {
        return method.invoke(null, args);
    }
}
