package ru.timeconqueror.timecore.util.reflection;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Wrapper for field, unlocks the access to it.
 *
 * @param <T> field type.
 */
public class UnlockedField<T> {
    private final Field field;
    private boolean finalized;

    public UnlockedField(Field field) {
        this.field = field;

        ReflectionHelper.setAccessible(field);
        finalized = ReflectionHelper.isFinal(field);
    }

    /**
     * Gets the value of field in provided {@code fieldOwner}
     * Safe for use with non-accessible fields.
     *
     * @param fieldOwner owner of field. If the underlying field is static, the obj argument is ignored; it may be null.
     */
    @SuppressWarnings("unchecked")
    public T get(@Nullable Object fieldOwner) {
        try {
            return (T) field.get(fieldOwner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets new value into field of provided {@code fieldOwner}
     * Safe for use with non-accessible fields.
     *
     * @param fieldOwner owner of field. If the underlying field is static, the methodOwner argument is ignored; it may be null.
     * @param newVal     new value to put in the field of provided {@code fieldOwner}
     */
    public void set(@Nullable Object fieldOwner, T newVal) {
        if (finalized) {
            try {
                ReflectionHelper.unfinalize(field);
                finalized = false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            field.set(fieldOwner, newVal);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getField() {
        return field;
    }
}
