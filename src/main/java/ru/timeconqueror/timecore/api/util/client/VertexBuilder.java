package ru.timeconqueror.timecore.api.util.client;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public interface VertexBuilder {
    void apply(Matrix4f pose, Matrix3f normal, float x, float y, float u1, float v1, float u2, float v2);
}
