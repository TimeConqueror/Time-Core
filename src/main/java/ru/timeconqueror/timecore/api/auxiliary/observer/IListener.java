package ru.timeconqueror.timecore.api.auxiliary.observer;

public interface IListener<T> {
    void update(T oldValue, T newValue);
}
