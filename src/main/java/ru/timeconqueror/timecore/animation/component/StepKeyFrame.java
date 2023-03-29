package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;

public class StepKeyFrame implements IKeyFrame {
    private final int time;
    private final Vector3f pre;
    private final Vector3f post;

    public StepKeyFrame(int time, Vector3f pre, Vector3f post) {
        this.time = time;
        this.pre = pre;
        this.post = post;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public Vector3f getVec(KeyFrameState state) {
        return state == KeyFrameState.PREV ? post : pre;
    }

    @Override
    public IKeyFrame withNewTime(int time) {
        return new StepKeyFrame(time, pre, post);
    }
}
