package ru.timeconqueror.timecore.client.render.model.uv;

import net.minecraft.util.Direction;
import ru.timeconqueror.timecore.client.render.model.FaceUVDefinition;

import java.util.Map;

public class FacedUVResolver implements UVResolver {
    private final Map<Direction, FaceUVDefinition> mappings;

    public FacedUVResolver(Map<Direction, FaceUVDefinition> mappings) {
        this.mappings = mappings;
    }

    @Override
    public SizedUV get(Direction direction) {
        FaceUVDefinition uvDefinition = mappings.get(direction);
        if (uvDefinition == null) throw new IllegalStateException("How is that possible?");

        return new SizedUV(uvDefinition.uv().x(), uvDefinition.uv().y(), uvDefinition.uv().x() + uvDefinition.size().x(), uvDefinition.uv().y() + uvDefinition.size().y());
    }
}
