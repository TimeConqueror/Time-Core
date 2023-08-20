package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.molang.Molang;

public class DynamicVector implements Vector {
    private final MolangExpression[] dynamicVec;

    public DynamicVector(MolangExpression... dynamicVec) {
        if (dynamicVec.length != 1 && dynamicVec.length != 3) {
            throw new IllegalArgumentException();
        }

        this.dynamicVec = dynamicVec;
    }

    @Override
    public Vector3f get(MolangEnvironment env) {
        if (dynamicVec.length == 1) {
            var f = Molang.resolve(env, dynamicVec[0]);
            return new Vector3f(f, f, f);
        }

        return new Vector3f(
                Molang.resolve(env, dynamicVec[0]),
                Molang.resolve(env, dynamicVec[1]),
                Molang.resolve(env, dynamicVec[2])
        );
    }
}
