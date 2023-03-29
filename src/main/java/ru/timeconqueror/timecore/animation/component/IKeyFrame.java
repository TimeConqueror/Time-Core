package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;

public interface IKeyFrame {
    int getTime();

    Vector3f getVec(KeyFrameState state);

    IKeyFrame withNewTime(int time);
}
