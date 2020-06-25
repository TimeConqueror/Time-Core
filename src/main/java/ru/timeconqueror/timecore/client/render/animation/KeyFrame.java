package ru.timeconqueror.timecore.client.render.animation;

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

    public int getStartTime() {
        return startTime;
    }

    public Vector3f getVec() {
        return vec;
    }

    public static KeyFrame createIdleKeyFrame(int startTime, float modelIdleX, float modelIdleY, float modelIdleZ) {
        return new KeyFrame(startTime, new Vector3f(modelIdleX, modelIdleY, modelIdleZ));
    }

    public KeyFrame withNewStartTime(int startTime) {
        return new KeyFrame(startTime, vec);
    }
}
