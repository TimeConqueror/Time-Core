package ru.timeconqueror.timecore.api.auxiliary.observer;

public interface IObservable<T> {
    void addListener(IListener<T> observer);

    void removeListener(IListener<T> observer);

    void notifyListeners();

    T getValue();

    void setValue(T value);
}
