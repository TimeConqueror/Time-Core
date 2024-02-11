package ru.timeconqueror.timecore.animation.clock;

import ru.timeconqueror.timecore.api.animation.Clock;

public class HandClock implements Clock {
    private long currentMillis;

    @Override
    public long getMillis() {
        return currentMillis;
    }

    public void setMillis(long millis) {
        this.currentMillis = millis;
    }
}
