package ru.timeconqueror.timecore.api.registry.base;

import ru.timeconqueror.timecore.api.registry.TimeRegister;
import ru.timeconqueror.timecore.api.util.Temporal;

import java.util.ArrayList;
import java.util.List;

public abstract class RunnableStoringRegister extends TimeRegister {
    private final Temporal<List<Runnable>> runnables = Temporal.of(new ArrayList<>());

    public RunnableStoringRegister(String modId) {
        super(modId);
    }

    protected void add(Runnable runnable) {
        runnables.get().add(runnable);
    }

    protected void runAll() {
        runnables.transferAndRemove(runnables -> {
            for (Runnable task : runnables) {
                task.run();
            }
        });
    }
}
