package ru.timeconqueror.timecore.animation.clock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.timeconqueror.timecore.api.animation.Clock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemMillisClock implements Clock {
    public static final SystemMillisClock INSTANCE = new SystemMillisClock();

    @Override
    public long getMillis() {
        return System.currentTimeMillis();
    }
}
