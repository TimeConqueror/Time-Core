package ru.timeconqueror.timecore.animation.clock;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Clock;

@AllArgsConstructor
@NoArgsConstructor
public class TickBasedClock implements Clock {
    private long ticksExisted = 0;

    @Override
    public long getMillis(float partialTick) {
        return AnimationUtils.ticksToMillis(ticksExisted + partialTick);
    }

    public void tick() {
        ticksExisted++;
    }
}
