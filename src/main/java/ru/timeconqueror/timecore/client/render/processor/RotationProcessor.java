package ru.timeconqueror.timecore.client.render.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.model.PartLink;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RotationProcessor<T> implements IModelProcessor<T> {
    private final PartLink partLink;
    private final Vector3fc rotationRadians;

    public static <T> RotationProcessor<T> byDegrees(PartLink partLink, Direction.Axis axis, float degrees) {
        return byDegrees(partLink, new Vector3f(axis == Direction.Axis.X ? degrees : 0,
                axis == Direction.Axis.Y ? degrees : 0,
                axis == Direction.Axis.Z ? degrees : 0));
    }

    public static <T> RotationProcessor<T> byDegrees(PartLink partLink, Vector3fc degrees) {
        return byRadians(partLink, new Vector3f(MathUtils.toRadians(degrees.x()), MathUtils.toRadians(degrees.y()), MathUtils.toRadians(degrees.z())));
    }

    public static <T> RotationProcessor<T> byRadians(PartLink partLink, Vector3fc radians) {
        return new RotationProcessor<>(partLink, radians);
    }

    @Override
    public void process(T object, ITimeModel model, float partialTick) {
        partLink.getPart(model).getRotation().add(rotationRadians);
    }
}
