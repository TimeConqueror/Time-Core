package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.MolangExpression;
import lombok.AllArgsConstructor;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.molang.Molang;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.function.Function;

@AllArgsConstructor
public enum Channel {
    ROTATION(part -> new Vector3f(0, 0, 0)),//TODO getRotation?
    TRANSLATION(TimeModelPart::getTranslation),
    SCALE(TimeModelPart::getScale);

    private final Function<TimeModelPart, Vector3f> makeDefaultVector;

    public Vector3f getDefaultVector(TimeModelPart part) {
        return makeDefaultVector.apply(part);
    }

    public MolangExpression fromBedrockFormat(Direction.Axis axis, MolangExpression expression) {
        return switch (this) {
            case ROTATION -> switch (axis) {
                case X, Y -> MolangExpression.of(env -> -MathUtils.toRadians(Molang.resolve(env, expression)));
                case Z -> MolangExpression.of(env -> MathUtils.toRadians(Molang.resolve(env, expression)));
            };
            case TRANSLATION -> switch (axis) {
                case X -> MolangExpression.of(env -> -Molang.resolve(env, expression));
                case Y, Z -> MolangExpression.of(env -> Molang.resolve(env, expression));
            };
            case SCALE -> expression;
        };
    }

    public Vector3f fromBedrockFormat(Vector3f vec) {
        return switch (this) {
            case ROTATION -> vec
                    .set(MathUtils.toRadians(vec.x()), MathUtils.toRadians(vec.y()), MathUtils.toRadians(vec.z()))
                    .mul(-1, -1, 1);
            case TRANSLATION -> vec.mul(-1, 1, 1);
            case SCALE -> vec;
        };
    }
}
