package ru.timeconqueror.timecore.animation.component;

import net.minecraft.client.renderer.Vector3f;

public class KeyFrame {
    /**
     * in ms
     */
    private final int startTime;
    private final Vector3f vec;

    public KeyFrame(int startTime, Vector3f vec) {
        this.startTime = startTime;
        this.vec = vec;
    }

    public static KeyFrame createIdleKeyFrame(int startTime, Vector3f modelIdleVec) {
        return new KeyFrame(startTime, modelIdleVec);
    }

    public int getStartTime() {
        return startTime;
    }

    public Vector3f getVec() {
        return vec;
    }

    public KeyFrame withNewStartTime(int startTime) {
        return new KeyFrame(startTime, vec);
    }
}
