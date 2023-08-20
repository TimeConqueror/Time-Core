package ru.timeconqueror.timecore.animation.loading;

import gg.moonflower.molangcompiler.api.MolangExpression;
import lombok.AllArgsConstructor;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.component.ConstantVector;
import ru.timeconqueror.timecore.animation.component.DynamicVector;
import ru.timeconqueror.timecore.animation.component.Vector;
import ru.timeconqueror.timecore.api.animation.Channel;

public interface VectorDefinition {

    Vector build(Channel channel);

    @AllArgsConstructor
    class ConstantVectorDefinition implements VectorDefinition {
        private final Vector3f vec;

        @Override
        public Vector build(Channel channel) {
            return new ConstantVector(channel.fromBedrockFormat(vec));
        }
    }

    class DynamicVectorDefinition implements VectorDefinition {
        private final MolangExpression[] expressions;

        public DynamicVectorDefinition(MolangExpression... expressions) {
            this.expressions = expressions;
        }

        @Override
        public Vector build(Channel channel) {
            MolangExpression[] expressions = new MolangExpression[3];

            for (int i = 0; i < expressions.length; i++) {
                var expression = this.expressions.length == 3 ? this.expressions[i] : this.expressions[0];
                expressions[i] = channel.fromBedrockFormat(Direction.Axis.VALUES[i], expression);
            }

            return new DynamicVector(expressions);
        }
    }
}
