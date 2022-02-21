package ru.timeconqueror.timecore.api.registry.base;

import net.minecraftforge.eventbus.api.Event;
import ru.timeconqueror.timecore.api.util.Temporal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskHolder<T> extends Temporal<List<T>> {
    private TaskHolder(List<T> value, String errorMessage) {
        super(value, errorMessage);
    }

    public void add(T element) {
        get().add(element);
    }

    public void doForEachAndRemove(Consumer<T> action) {
        doAndRemove(ts -> ts.forEach(action));
    }

    public static <T> TaskHolder<T> make(Class<? extends Event> eventClass) {
        return new TaskHolder<>(new ArrayList<>(), "You attempted to access tasks for event '" + eventClass.getName() + "' while it has been already fired.");
    }
}