package ru.timeconqueror.timecore.storage;

import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;
import ru.timeconqueror.timecore.util.Temporal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoadingOnlyStorage {
    private static final Temporal<List<TimeResourceHolder>> HOLDERS = Temporal.of(new ArrayList<>(), "Holders were already loaded. You need to register holder earlier!");
    public static final Lock HOLDER_SYSTEM_LOCK = new ReentrantLock();

    public static void addResourceHolder(TimeResourceHolder holder) {
        HOLDER_SYSTEM_LOCK.lock();

        try {
            HOLDERS.get().add(holder);
        } finally {
            HOLDER_SYSTEM_LOCK.unlock();
        }
    }

    public static void loadResourceHolders() {
        HOLDER_SYSTEM_LOCK.lock();

        try {
            GlobalResourceStorage.INSTANCE.fill(HOLDERS.remove());
        } finally {
            HOLDER_SYSTEM_LOCK.unlock();
        }
    }
}
