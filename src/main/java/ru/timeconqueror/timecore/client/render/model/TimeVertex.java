package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.util.math.vector.Vector3f;

public class TimeVertex {
    private final Vector3f pos;
    private final float u;
    private final float v;

    public TimeVertex(Vector3f pos, float u, float v) {
        this.pos = pos;
        this.u = u;
        this.v = v;
    }

    public Vector3f getPos() {
        return pos;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }
}
