package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class TimeQuad {
    public final TimeVertex[] vertices;
    public final Vector3f normal;

    public TimeQuad(TimeVertex[] vertices, boolean mirror, Direction direction) {
        this.vertices = vertices;
        this.normal = direction.step();
        if (mirror) {
            this.normal.mul(-1.0F, 1.0F, 1.0F);
        }
    }
}
