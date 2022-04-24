package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.animation.Channel;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.List;

public class BoneOption {
    private final String boneName;
    /**
     * Immutable rotation keyframe list
     */
    private final List<IKeyFrame> rotations;
    /**
     * Immutable position keyframe list
     */
    private final List<IKeyFrame> positions;
    /**
     * Immutable scale keyframe list
     */
    private final List<IKeyFrame> scales;

    public BoneOption(String boneName, List<IKeyFrame> rotations, List<IKeyFrame> positions, List<IKeyFrame> scales) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.positions = positions;
        this.scales = scales;
    }

    public void apply(Animation animation, AnimationLayer layer, TimeModelPart piece, int existingTime) {
        Vector3f rotateVec = KeyFrameInterpolator.findInterpolationVec(animation, rotations, existingTime);
        if (rotateVec != null) {
            AnimationUtils.applyRotation(piece, layer, rotateVec);
        }

        Vector3f posVec = KeyFrameInterpolator.findInterpolationVec(animation, positions, existingTime);
        if (posVec != null) {
            AnimationUtils.applyOffset(piece, layer, posVec);
        }

        Vector3f scaleVec = KeyFrameInterpolator.findInterpolationVec(animation, scales, existingTime);
        if (scaleVec != null) {
            AnimationUtils.applyScale(piece, layer, scaleVec);
        }
    }

    public String getName() {
        return boneName;
    }

    public List<IKeyFrame> getKeyFrames(Channel channel) {
        if (channel == Channel.ROTATION) {
            return rotations;
        } else if (channel == Channel.POSITION) {
            return positions;
        } else if (channel == Channel.SCALE) {
            return scales;
        }

        throw new IllegalArgumentException("Unknown channel: " + channel);
    }
}
