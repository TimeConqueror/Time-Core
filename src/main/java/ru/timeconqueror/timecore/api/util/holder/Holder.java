package ru.timeconqueror.timecore.api.util.holder;

import java.util.Objects;

/**
 * Provides ability to set variable created outside of lambda function from this lambda.
 * <br>Example:<br>
 * <blockquote><pre>
 * Holder&lt;Boolean&gt; passed = new Holder<>(false);
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
public class Holder<T> {
    private T value;

    public Holder(T value) {
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
        if (!(o instanceof Holder<?> holder)) return false;
        return Objects.equals(value, holder.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
