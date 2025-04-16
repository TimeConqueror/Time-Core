package ru.timeconqueror.timecore.animation.clock;

import ru.timeconqueror.timecore.api.animation.Clock;

public class SystemMillisClock implements Clock {
    private final long startMillis = System.currentTimeMillis();

    @Override
    public long getMillis(float partialTick) {
        return System.currentTimeMillis() - startMillis;
    }
}
