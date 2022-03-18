package ru.timeconqueror.timecore.client.render.model.uv;

import net.minecraft.core.Direction;

public interface UVResolver {
    SizedUV get(Direction direction);

    record SizedUV(int u1, int v1, int u2, int v2) {
        public static SizedUV fromFloats(float u1, float v1, float u2, float v2) {
            return new SizedUV(
                    (int) Math.ceil(u1),
                    (int) Math.ceil(v1),
                    (int) Math.ceil(u2),
                    (int) Math.ceil(v2)
            );
        }
    }
}