package ru.timeconqueror.timecore.client.render.model;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

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
