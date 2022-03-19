package ru.timeconqueror.timecore.client.render.model.uv;

import net.minecraft.util.Direction;

public interface UVResolver {
    SizedUV get(Direction direction);

    class SizedUV {
        private final int u1;
        private final int v1;
        private final int u2;
        private final int v2;

        public SizedUV(int u1, int v1, int u2, int v2) {
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }

        public int u1() {
            return u1;
        }

        public int v1() {
            return v1;
        }

        public int u2() {
            return u2;
        }

        public int v2() {
            return v2;
        }

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