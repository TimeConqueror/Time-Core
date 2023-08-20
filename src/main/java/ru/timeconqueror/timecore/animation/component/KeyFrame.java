package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.joml.Vector3f;

public class KeyFrame implements IKeyFrame {
    /**
     * in ms
     */
    private final int time;
    private final Vector vec;

    public KeyFrame(int time, Vector vec) {
        this.time = time;
        this.vec = vec;
    }

    public static KeyFrame create(int startTime, Vector vec) {
        return new KeyFrame(startTime, vec);
    }

    public static KeyFrame createSimple(int startTime, Vector3f vec) {
        return new KeyFrame(startTime, new ConstantVector(vec));
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public Vector3f getVec(MolangEnvironment env, KeyFrameState state) {
        return vec.get(env);
    }

    @Override
    public String toString() {
        return "KeyFrame{" +
                "time=" + time +
                ", vec=" + vec +
                '}';
    }
}
