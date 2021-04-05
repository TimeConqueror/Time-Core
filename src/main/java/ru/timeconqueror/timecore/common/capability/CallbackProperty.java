package ru.timeconqueror.timecore.common.capability;

import ru.timeconqueror.timecore.common.capability.property.IChangable;

//TODO make inner properties instead of this duct tape
public class CallbackProperty<T> {
    private final IChangable boundProperty;
    private T value;

    public CallbackProperty(IChangable boundProperty, T value) {
        this.boundProperty = boundProperty;
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        boundProperty.setChanged(true);
    }
}
