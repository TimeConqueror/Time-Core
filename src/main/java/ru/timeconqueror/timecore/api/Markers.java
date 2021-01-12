package ru.timeconqueror.timecore.api;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import ru.timeconqueror.timecore.api.util.CollectionUtils;

public class Markers {
    public static final Marker RESOURCE_SYSTEM = RegisteredMarker.RESOURCE_SYSTEM.getMarker();

    public static Marker[] getAll() {
        return CollectionUtils.mapArray(RegisteredMarker.values(), Marker[]::new, RegisteredMarker::getMarker);
    }

    private enum RegisteredMarker {
        RESOURCE_SYSTEM("RESOURCE_SYSTEM");

        private final Marker marker;

        RegisteredMarker(String name) {
            this.marker = MarkerManager.getMarker(name);
        }

        public Marker getMarker() {
            return marker;
        }
    }
}
