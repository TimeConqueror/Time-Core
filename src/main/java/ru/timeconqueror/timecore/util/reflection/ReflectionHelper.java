package ru.timeconqueror.timecore.util.reflection;

import com.google.common.annotations.Beta;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Beta
public class ReflectionHelper {
    public static boolean isFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static void unfinalize(Field f) throws IllegalAccessException {
        f.setInt(f, f.getModifiers() & ~Modifier.FINAL);
    }

    public static void setAccessible(Field f) {
        if (!f.isAccessible()) {
            f.setAccessible(true);
        }
    }

    public static void setAccessible(Method m) {
        if (!m.isAccessible()) {
            m.setAccessible(true);
        }
    }

    public static void setAccessible(Constructor<?> c) {
        if (!c.isAccessible()) {
            c.setAccessible(true);
        }
    }

    /**
     * Creates class from given name.
     *
     * @param name the fully qualified name of the desired class.
     * @return class from given name or null if class isn't found.
     */
    @Nullable
    public static Class<?> createClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a field with the specified name in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Returns null if the field is not found and prints error stacktrace.
     *
     * @param clazz     The class to find the field on.
     * @param fieldName The name of the field to find.
     * @return The field with the specified name in the given class or null if the field is not found.
     */
    @Nullable
    public static <T> UnlockedField<T> findField(Class<?> clazz, String fieldName) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            return new UnlockedField<>(f);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Only for fields, which come from vanilla minecraft!
     * <p>
     * Finds a field with the specified name in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Returns null if the field is not found and prints error stacktrace.
     *
     * @param clazz   The class to find the field on.
     * @param srgName The searge obfuscated name of the field to find.
     * @return The field with the specified name in the given class or null if the field is not found.
     */
    @Nullable
    public static <T, C> UnlockedField<T> findObfField(Class<C> clazz, String srgName) {
        try {
            Field f = ObfuscationReflectionHelper.findField(clazz, srgName);
            return new UnlockedField<>(f);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a method with the specified name and params in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Returns null if the method is not found and prints error stacktrace.
     *
     * @param clazz      The class to find the field on.
     * @param methodName The name of the method to find.
     * @param params     The parameter classes of the method to find.
     * @return The method with the specified name in the given class or null if the method is not found.
     */
    @Nullable
    public static <T> UnlockedMethod<T> findMethod(Class<?> clazz, String methodName, Class<?>... params) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, params);
            return new UnlockedMethod<>(method);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Only for methods, which come from vanilla minecraft!
     * <p>
     * Finds a method with the specified name and params in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Returns null if the method is not found and prints error stacktrace.
     *
     * @param clazz   The class to find the field on.
     * @param srgName The searge obfuscated name of the method to find.
     * @param params  The parameter classes of the method to find.
     * @return The method with the specified name in the given class or null if the method is not found.
     */
    @Nullable
    public static <T> UnlockedMethod<T> findObfMethod(Class<?> clazz, String srgName, Class<?>... params) {
        try {
            Method method = ObfuscationReflectionHelper.findMethod(clazz, srgName, params);
            return new UnlockedMethod<>(method);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a constructor in the specified class that has matching parameter types.
     *
     * @param clazz  The class to find the constructor in
     * @param params The parameter types of the constructor.
     * @param <T>    The type of constructor.
     * @return The constructor with specified params or null if the constructor is not found.
     */
    @Nullable
    public static <T> UnlockedConstructor<T> findConstructor(Class<T> clazz, Class<?>... params) {
        try {
            Constructor<T> constructor = ObfuscationReflectionHelper.findConstructor(clazz, params);
            return new UnlockedConstructor<>(constructor);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void initClass(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            TimeCore.LOGGER.error("Can't load class" + className + ", because it wasn't found");
        }
    }
}
