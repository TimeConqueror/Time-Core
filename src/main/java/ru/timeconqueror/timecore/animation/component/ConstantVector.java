package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.AllArgsConstructor;
import org.joml.Vector3f;

@AllArgsConstructor
public class ConstantVector implements Vector {
    private final Vector3f vec;

    @Override
    public Vector3f get(MolangEnvironment env) {
        return vec;
    }
}
