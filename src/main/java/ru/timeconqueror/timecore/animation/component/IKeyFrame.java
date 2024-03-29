package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.joml.Vector3f;

public interface IKeyFrame {
    int getTime();

    Vector3f getVec(MolangEnvironment env, KeyFrameState state);
}
