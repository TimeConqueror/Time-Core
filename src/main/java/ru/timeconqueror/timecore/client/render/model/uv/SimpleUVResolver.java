package ru.timeconqueror.timecore.client.render.model.uv;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import ru.timeconqueror.timecore.api.util.Vec2i;

public class SimpleUVResolver implements UVResolver {
    private final Vec2i uv;
    private final Vector3f size;


    public SimpleUVResolver(Vec2i uv, Vector3f size) {
        this.uv = uv;
        this.size = size;
    }

    //    @formatter:off
    @Override
    public UVResolver.SizedUV get(Direction direction) {
        switch (direction) {
            case EAST -> {  return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width(),         uv.y() + depth(), uv.x() + depth() + width() + depth(),          uv.y() + depth() + height());}
            case WEST -> {  return UVResolver.SizedUV.fromFloats(uv.x(),                                 uv.y() + depth(), uv.x() + depth(),                              uv.y() + depth() + height());}
            case DOWN -> {  return UVResolver.SizedUV.fromFloats(uv.x() + depth(),                       uv.y(),           uv.x() + depth() + width(),                    uv.y() + depth());}
            case UP -> {    return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width(),          uv.y() + depth(),uv.x() + depth() + width() + width(),              uv.y());}
            case NORTH -> { return UVResolver.SizedUV.fromFloats(uv.x() + depth(),                    uv.y() + depth(),uv.x() + depth() + width(),                    uv.y() + depth() + height());}
            case SOUTH -> { return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width() + depth(),uv.y() + depth(),uv.x() + depth() + width() + depth() + width(),uv.y() + depth() + height());}
        }

        throw new IllegalStateException("Shouldn't be reached");
    }
    //    @formatter:on

    public float width() {
        return size.x();
    }

    public float height() {
        return size.y();
    }

    public float depth() {
        return size.z();
    }
}
