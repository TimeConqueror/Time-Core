package ru.timeconqueror.timecore.animation.util;

import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.ILayer;
import ru.timeconqueror.timecore.api.util.VecUtils;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

public class AnimationUtils {
    public static float milliSecondsToTicks(int milliSeconds) {
        return milliSeconds / 1000F * 20;
    }

    public static void applyRotation(TimeModelPart piece, ILayer layer, Vector3f rotationIn) {
        BlendType blendType = layer.getBlendType();
        rotationIn.mul(layer.getWeight());

        if (blendType == BlendType.OVERWRITE) {
            piece.getRotation().set(piece.startRotationRadians);
        } else if (blendType != BlendType.ADD) throw new UnsupportedOperationException();

        piece.getRotation().add(rotationIn);
    }

    public static void applyOffset(TimeModelPart piece, ILayer layer, Vector3f offsetIn) {
        BlendType blendType = layer.getBlendType();
        offsetIn.mul(layer.getWeight());

        if (blendType == BlendType.OVERWRITE) {
            piece.getTranslation().set(offsetIn);
        } else if (blendType == BlendType.ADD) {
            piece.getTranslation().add(offsetIn);
        } else throw new UnsupportedOperationException();
    }

    public static void applyScale(TimeModelPart piece, ILayer layer, Vector3f scaleIn) {
        BlendType blendType = layer.getBlendType();
        float weight = layer.getWeight();

        scaleIn.set(calcWeightedScale(scaleIn.x(), weight),
                calcWeightedScale(scaleIn.y(), weight),
                calcWeightedScale(scaleIn.z(), weight));

        if (blendType == BlendType.OVERWRITE) {
            piece.getScale().set(scaleIn.x(), scaleIn.y(), scaleIn.z());
        } else if (blendType == BlendType.ADD) {
            piece.getScale().mul(scaleIn);
        } else throw new UnsupportedOperationException();
    }

    private static float calcWeightedScale(float scale, float weight) {
        return scale > 1 ? 1 + (scale - 1) * weight : 1 - (1 - scale) * weight;
    }
}
