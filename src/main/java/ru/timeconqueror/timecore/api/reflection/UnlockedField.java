package ru.timeconqueror.timecore.api.reflection;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Wrapper for field, tries to unlock the access to it.
 *
 * @param <O> owner type.
 * @param <T> field type.
 */
public class UnlockedField<O, T> {
    private final Field field;
    private final boolean isStatic;
    private final Boolean finalSettable;
    private final boolean accessible;
    private final boolean isFinal;

    public UnlockedField(Field field) {
        this.field = field;

        isFinal = ReflectionHelper.isFinal(field);
        isStatic = ReflectionHelper.isStatic(field);
        finalSettable = predictFinalSettable();

        field.setAccessible(true);
        accessible = field.isAccessible();
    }

    /**
     * From https://github.com/openjdk/jdk/pull/5027#issuecomment-969672078
     */
    private Boolean predictFinalSettable() {
        if (isFinal) {
            return !isStatic;
        }

        return null;
    }

    /**
     * Gets the value of field in provided {@code fieldOwner}
     *
     * @param fieldOwner owner of field. If the underlying field is static, the obj argument is ignored; it may be null.
     */
    @SuppressWarnings("unchecked")
    public T get(@Nullable O fieldOwner) {
        validateAccessible(AccessType.GET, true);

        if (!isStatic && fieldOwner == null) {
            throw new IllegalArgumentException(String.format("Tried to pass null as a fieldOwner to the non static field %s", field.toString()));
        }

        try {
            return (T) field.get(fieldOwner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets new value into field of provided {@code fieldOwner}
     *
     * @param fieldOwner owner of field. If the underlying field is static, the methodOwner argument is ignored; it may be null.
     * @param newVal     new value to put in the field of provided {@code fieldOwner}
     */
    public void set(@Nullable O fieldOwner, T newVal) {
        validateAccessible(AccessType.SET, true);

        if (!isStatic && fieldOwner == null) {
            throw new IllegalArgumentException(String.format("Tried to pass null as a fieldOwner to the non static field %s", field.toString()));
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

    /**
     * Returns true, if provided field is static, otherwise returns false.
     */
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String toString() {
        return field.toString();
    }

    public boolean guessIsAccessible(AccessType accessType) {
        return validateAccessible(accessType, false);
    }

    private boolean validateAccessible(AccessType accessType, boolean strict) {
        if (accessType == AccessType.SET) {
            if (isFinal && !finalSettable) {
                if (!strict) return false;

                throw new UnsupportedOperationException(String.format("\nFinal field object may have (but not necessarily) write access if at least: the field is non-static (%b);", !isStatic));
            }
        }

        if (!accessible) {
            if (!strict) return false;

            throw new UnsupportedOperationException("The field isn't accessible");
        }

        return true;
    }

    public enum AccessType {
        GET, SET
    }
}
