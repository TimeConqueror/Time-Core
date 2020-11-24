package ru.timeconqueror.timecore.storage;

import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;
import ru.timeconqueror.timecore.util.Temporal;

import java.util.ArrayList;
import java.util.List;

public class LoadingOnlyStorage {
    private static final Temporal<List<TimeResourceHolder>> HOLDERS = Temporal.of(new ArrayList<>(), "Holders were already loaded. You need to add it earlier!");

    public synchronized static void addResourceHolder(TimeResourceHolder holder) {
        HOLDERS.get().add(holder);
    }

    public synchronized static void tryLoadResourceHolders() {
        HOLDERS.transferAndRemove(GlobalResourceStorage.INSTANCE::fill);
    }
}
