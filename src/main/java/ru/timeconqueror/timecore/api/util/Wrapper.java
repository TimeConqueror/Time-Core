package ru.timeconqueror.timecore.api.util;

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
        return value != null ? value.toString() : null;
    }
}
