package ru.timeconqueror.timecore.api.animation;

public enum BlendType {
    /**
     * Information from other layers will be ignored.
     * All animation positions, rotations, scales on this layer will overwrite all positions, rotations, scales
     * that were applied by the animations on the previous layers.
     */
    OVERWRITE,
    /**
     * The animation will be added on top of previous layers.
     * All animation positions, rotations, scales on this layer will be added to the corresponding ones
     * from the animations on the previous layers.
     */
    ADD
}