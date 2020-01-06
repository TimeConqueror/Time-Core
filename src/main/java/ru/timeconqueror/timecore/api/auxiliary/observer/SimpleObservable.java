package ru.timeconqueror.timecore.api.auxiliary.observer;

import java.util.ArrayList;

public class SimpleObservable<T> implements IObservable<T> {
    private T value;
    private T prevValue;
    private ArrayList<IListener<T>> observers = new ArrayList<>();

    public SimpleObservable(T value) {
        this.value = value;
        this.prevValue = value;
    }

    @Override
    public void addListener(IListener<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeListener(IListener<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyListeners() {
        for (IListener<T> observer : observers) {
            observer.update(prevValue, value);
        }
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        prevValue = this.value;
        this.value = value;

        notifyListeners();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
