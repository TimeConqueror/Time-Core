package ru.timeconqueror.timecore.animation.util;

import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

public class AnimationUtils {
    public static float milliSecondsToTicks(int milliSeconds) {
        return milliSeconds / 1000F * 20;
    }

    public static void applyRotation(TimeModelRenderer piece, AnimationLayer layer, Vector3f rotationIn) {
        BlendType blendType = layer.getBlendType();
        rotationIn.mul(layer.getWeight());

        if (blendType == BlendType.OVERRIDE) {
            piece.xRot = piece.startRotationRadians.x() + rotationIn.x();
            piece.yRot = piece.startRotationRadians.y() + rotationIn.y();
            piece.zRot = piece.startRotationRadians.z() + rotationIn.z();
        } else if (blendType == BlendType.ADDING) {
            piece.xRot += rotationIn.x();
            piece.yRot += rotationIn.y();
            piece.zRot += rotationIn.z();
        } else throw new UnsupportedOperationException();
    }

    public static void applyOffset(TimeModelRenderer piece, AnimationLayer layer, Vector3f offsetIn) {
        BlendType blendType = layer.getBlendType();
        offsetIn.mul(layer.getWeight());

        if (blendType == BlendType.OVERRIDE) {
            piece.offset = offsetIn;
        } else if (blendType == BlendType.ADDING) {
            piece.offset.add(offsetIn);
        } else throw new UnsupportedOperationException();
    }

    public static void applyScale(TimeModelRenderer piece, AnimationLayer layer, Vector3f scaleIn) {
        BlendType blendType = layer.getBlendType();
        float weight = layer.getWeight();

        scaleIn.set(calcWeightedScale(scaleIn.x(), weight),
                calcWeightedScale(scaleIn.y(), weight),
                calcWeightedScale(scaleIn.z(), weight));

        if (blendType == BlendType.OVERRIDE) {
            piece.setScaleFactor(scaleIn.x(), scaleIn.y(), scaleIn.z());
        } else if (blendType == BlendType.ADDING) {
            Vector3f currentScale = piece.getScaleFactor();
            piece.setScaleFactor(scaleIn.x() * currentScale.x(), scaleIn.y() * currentScale.y(), scaleIn.z() * currentScale.z());
        } else throw new UnsupportedOperationException();
    }

    private static float calcWeightedScale(float scale, float weight) {
        return scale > 1 ? 1 + (scale - 1) * weight : 1 - (1 - scale) * weight;
    }
}
