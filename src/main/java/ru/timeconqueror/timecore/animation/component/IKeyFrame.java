package ru.timeconqueror.timecore.animation.component;

import org.joml.Vector3f;

public interface IKeyFrame {
    int getTime();

    Vector3f getVec(KeyFrameState state);

    IKeyFrame withNewTime(int time);
}
