package ru.timeconqueror.timecore.animation.component;

import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.List;

public class BoneOption {
    private final String boneName;
    /**
     * Immutable rotation keyframe list
     */
    private final List<KeyFrame> rotations;
    /**
     * Immutable position keyframe list
     */
    private final List<KeyFrame> positions;
    /**
     * Immutable scale keyframe list
     */
    private final List<KeyFrame> scales;

    public BoneOption(String boneName, List<KeyFrame> rotations, List<KeyFrame> positions, List<KeyFrame> scales) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.positions = positions;
        this.scales = scales;
    }

    public void apply(Animation animation, AnimationLayer layer, TimeModelPart piece, int existingTime) {
        Vector3f rotateVec = KeyFrameInterpolator.findInterpolationVec(animation, rotations, new Vector3f(0, 0, 0), existingTime);
        if (rotateVec != null) {
            AnimationUtils.applyRotation(piece, layer, rotateVec);
        }

        Vector3f posVec = KeyFrameInterpolator.findInterpolationVec(animation, positions, piece.offset, existingTime);
        if (posVec != null) {
            AnimationUtils.applyOffset(piece, layer, posVec);
        }

        Vector3f currentScale = piece.getScaleFactor();
        Vector3f scaleVec = KeyFrameInterpolator.findInterpolationVec(animation, scales, currentScale, existingTime);
        if (scaleVec != null) {
            AnimationUtils.applyScale(piece, layer, scaleVec);
        }
    }

    public List<KeyFrame> getPositions() {
        return positions;
    }

    public List<KeyFrame> getRotations() {
        return rotations;
    }

    public List<KeyFrame> getScales() {
        return scales;
    }

    public String getName() {
        return boneName;
    }
}
