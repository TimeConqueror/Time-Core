package ru.timeconqueror.timecore.client.render.animation;

import net.minecraft.client.renderer.Vector3f;

public class KeyFrame {
    /**
     * in ms
     */
    private int startTime;
    private Vector3f vec;

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
}
