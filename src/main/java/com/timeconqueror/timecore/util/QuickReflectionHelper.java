package com.timeconqueror.timecore.util;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class QuickReflectionHelper {
    //for protecеed and private methods
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static MethodHandle findFieldGetter(Class<?> clazz, String... fieldNames) {
        final Field field = ReflectionHelper.findField(clazz, fieldNames);

        try {
            return lookup.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static MethodHandle findFieldSetter(Class<?> clazz, String... fieldNames) {
        final Field field = ReflectionHelper.findField(clazz, fieldNames);

        try {
            return lookup.unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static <T> MethodHandle findMethod(Class<T> clazz, String methodName, String methodObfName, Class<?>... methodTypes) {
        final Method method = ReflectionHelper.findMethod(clazz, methodName, methodObfName, methodTypes);

        try {
            return lookup.unreflect(method);
        } catch (IllegalAccessException e) {
            throw new ReflectionHelper.UnableToFindMethodException(new String[]{methodName, methodObfName}, e);
        }
    }
}
