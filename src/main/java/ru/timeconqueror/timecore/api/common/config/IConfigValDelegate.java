package ru.timeconqueror.timecore.api.common.config;

public interface IConfigValDelegate<T> {
    T get();

    void set(T val);
}