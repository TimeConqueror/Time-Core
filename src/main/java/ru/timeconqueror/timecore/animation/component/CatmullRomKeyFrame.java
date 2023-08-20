package ru.timeconqueror.timecore.animation.component;

/**
 * Keyframe with smooth interpolation.
 */
public class CatmullRomKeyFrame extends KeyFrame {
    public CatmullRomKeyFrame(int startTime, Vector vec) {
        super(startTime, vec);
    }
}
