package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.Channel;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.List;

public class AnimationBone {
    private final String boneName;
    /**
     * Immutable rotation keyframe list
     */
    private final List<IKeyFrame> rotations;
    /**
     * Immutable translation keyframe list
     */
    private final List<IKeyFrame> translations;
    /**
     * Immutable scale keyframe list
     */
    private final List<IKeyFrame> scales;

    public AnimationBone(String boneName, List<IKeyFrame> rotations, List<IKeyFrame> translations, List<IKeyFrame> scales) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.translations = translations;
        this.scales = scales;
    }

    public void apply(Animation animation, BlendType blendType, float weight, TimeModelPart piece, MolangEnvironment env, int existingTime) {
        Vector3f rotateVec = KeyFrameInterpolator.findInterpolationVec(animation, env, rotations, existingTime);
        if (rotateVec != null) {
            AnimationUtils.applyRotation(piece, blendType, weight, rotateVec);
        }

        Vector3f posVec = KeyFrameInterpolator.findInterpolationVec(animation, env, translations, existingTime);
        if (posVec != null) {
            AnimationUtils.applyOffset(piece, blendType, weight, posVec);
        }

        Vector3f scaleVec = KeyFrameInterpolator.findInterpolationVec(animation, env, scales, existingTime);
        if (scaleVec != null) {
            AnimationUtils.applyScale(piece, blendType, weight, scaleVec);
        }
    }

    public String getName() {
        return boneName;
    }

    public List<IKeyFrame> getKeyFrames(Channel channel) {
        if (channel == Channel.ROTATION) {
            return rotations;
        } else if (channel == Channel.TRANSLATION) {
            return translations;
        } else if (channel == Channel.SCALE) {
            return scales;
        }

        throw new IllegalArgumentException("Unknown channel: " + channel);
    }
}
