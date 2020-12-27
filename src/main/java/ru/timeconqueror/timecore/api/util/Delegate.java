package ru.timeconqueror.timecore.api.util;

public interface Delegate<T> {
    T get();

    void set(T val);
}