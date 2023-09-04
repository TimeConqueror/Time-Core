package ru.timeconqueror.timecore.animation.watcher;

import java.util.EnumSet;

public class FreezableTime {
    private long time;
    private long freezingTime = -1;
    private final EnumSet<FreezeCause> freezeCauses = EnumSet.noneOf(FreezeCause.class);

    public FreezableTime(long time) {
        this.time = time;
    }

    public void freeze(FreezeCause cause) {
        if (freezingTime == -1) {
            freezingTime = System.currentTimeMillis();
        }

        freezeCauses.add(cause);
    }

    public void unfreeze(FreezeCause cause) {
        freezeCauses.remove(cause);

        if(freezeCauses.isEmpty()) {
            if (freezingTime != -1) {
                time += System.currentTimeMillis() - freezingTime;
                freezingTime = -1;
            }
        }
    }

    public long get() {
        if (freezingTime != -1) {
            return time + (System.currentTimeMillis() - freezingTime);
        } else {
            return time;
        }
    }

    public void set(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "FreezableTime{" +
                "startTime=" + time + "ms" +
                ", beingFrozen=" + (System.currentTimeMillis() - freezingTime) + "ms" +
                '}';
    }

    public enum FreezeCause {
        GAME_PAUSED,
        IN_TRANSITION,
        MANUAL
    }
}
