package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;

public class KeyFrame {
    /**
     * in ms
     */
    private final int time;
    private final Vector3f vec;

    public KeyFrame(int time, Vector3f vec) {
        this.time = time;
        this.vec = vec;
    }

    public static KeyFrame createIdleKeyFrame(int startTime, Vector3f modelIdleVec) {
        return new KeyFrame(startTime, modelIdleVec);
    }

    public int getTime() {
        return time;
    }

    public Vector3f getVec() {
        return vec;
    }

    public KeyFrame withNewTime(int time) {
        return new KeyFrame(time, vec);
    }
}
