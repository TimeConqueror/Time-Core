package ru.timeconqueror.timecore.api.molang;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.molangcompiler.api.object.MolangObject;
import ru.timeconqueror.timecore.animation.Layer;

public class MolangFillers {

    public static void addAnimationBasedQueries(Layer layer, MolangObject object) {
        if (!object.isMutable())
            throw new MolangRuntimeException("Object %s is not mutable".formatted(object.getClass()));

        object.set(Molang.Query.ANIM_TIME, MolangExpression.of(() -> layer.getWatcherInfo().getAnimationTime() / 1000F));
    }
}
