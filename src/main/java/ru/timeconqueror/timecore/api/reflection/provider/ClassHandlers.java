package ru.timeconqueror.timecore.api.reflection.provider;

import org.jetbrains.annotations.Nullable;

public enum ClassHandlers {
    KOTLIN(KotlinClassHandler.INSTANCE),
    JAVA(new JavaClassHandler());

    private final ClassHandler provider;

    ClassHandlers(ClassHandler provider) {
        this.provider = provider;
    }

    public ClassHandler get() {
        return provider;
    }

    @Nullable
    public static ClassHandler findHandler(Class<?> clazz) {
        for (ClassHandlers provider : ClassHandlers.values()) {
            if (provider.get().canHandle(clazz)) {
                return provider.get();
            }
        }

        return null;
    }
}