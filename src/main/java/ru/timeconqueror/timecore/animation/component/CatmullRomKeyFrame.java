package ru.timeconqueror.timecore.animation.component;

import net.minecraft.util.math.vector.Vector3f;

/**
 * Keyframe with smooth interpolation.
 */
public class CatmullRomKeyFrame extends KeyFrame {
    public CatmullRomKeyFrame(int startTime, Vector3f vec) {
        super(startTime, vec);
    }
}
