package ru.timeconqueror.timecore.api.util;

import java.util.Objects;

/**
 * Provides ability to set variable created outside of lambda function from this lambda.
 * <br>Example:<br>
 * <blockquote><pre>
 * Wrapper&lt;Boolean&gt; passed = new Wrapper<>(false);
 *
 * someMethod(it -> {
 *     passed.set(true);
 * });
 *
 * if(passed.get()){
 *     doSmth();
 * }
 *
 * </pre></blockquote>
 */
public class Wrapper<T> {
    private T value;

    public Wrapper(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wrapper)) return false;
        Wrapper<?> wrapper = (Wrapper<?>) o;
        return Objects.equals(value, wrapper.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
