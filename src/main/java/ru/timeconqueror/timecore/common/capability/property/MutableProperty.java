package ru.timeconqueror.timecore.common.capability.property;

public interface MutableProperty {
    boolean isChanged();
    void setChanged(boolean changed);
}
