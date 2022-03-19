package ru.timeconqueror.timecore.client.render.model.uv;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.api.util.Vec2i;

public class BoxUVResolver implements UVResolver {
    private final Vec2i uv;
    private final Vector3f size;

    public BoxUVResolver(Vec2i uv, Vector3f size) {
        this.uv = uv;
        this.size = size;
    }

    //    @formatter:off
    @Override
    public UVResolver.SizedUV get(Direction direction) {
        switch (direction) {
            case EAST:  return UVResolver.SizedUV.fromFloats(    uv.x(),                              uv.y() + depth(), uv.x() + depth(),                              uv.y() + depth() + height())    ;
            case WEST:  return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width(),          uv.y() + depth(), uv.x() + depth() + width() + depth(),          uv.y() + depth() + height())    ;
            case DOWN:  return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width(),          uv.y() + depth(), uv.x() + depth() + width() + width(),              uv.y())                         ;
            case UP:    return UVResolver.SizedUV.fromFloats(uv.x() + depth(),                        uv.y(),          uv.x() + depth() + width(),                     uv.y() + depth())               ;
            case NORTH: return UVResolver.SizedUV.fromFloats(uv.x() + depth(),                    uv.y() + depth(), uv.x() + depth() + width(),                    uv.y() + depth() + height())    ;
            case SOUTH: return UVResolver.SizedUV.fromFloats(uv.x() + depth() + width() + depth(),uv.y() + depth(), uv.x() + depth() + width() + depth() + width(),uv.y() + depth() + height())    ;
        }

        throw new IllegalStateException("Shouldn't be reached");
    }
    //    @formatter:on

    public int width() {
        return (int) size.x();
    }

    public int height() {
        return (int) size.y();
    }

    public int depth() {
        return (int) size.z();
    }
}
