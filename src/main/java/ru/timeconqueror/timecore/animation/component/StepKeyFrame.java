package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.joml.Vector3f;

public class StepKeyFrame implements IKeyFrame {
    private final int time;
    private final Vector pre;
    private final Vector post;

    public StepKeyFrame(int time, Vector pre, Vector post) {
        this.time = time;
        this.pre = pre;
        this.post = post;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public Vector3f getVec(MolangEnvironment env, KeyFrameState state) {
        return state == KeyFrameState.PREV ? post.get(env) : pre.get(env);
    }
}
