package ru.timeconqueror.timecore.api.util.observer;

public interface IListener<T> {
    void update(T oldValue, T newValue);
}
