package ru.timeconqueror.timecore.animation.component;

import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;

public class BoneOption {
    private final String boneName;
    /**
     * Immutable rotation keyframe list
     */
    @Nullable
    private final List<KeyFrame> rotations;
    /**
     * Immutable position keyframe list
     */
    @Nullable
    private final List<KeyFrame> positions;
    /**
     * Immutable scale keyframe list
     */
    @Nullable
    private final List<KeyFrame> scales;

    public BoneOption(String boneName, @Nullable List<KeyFrame> rotations, @Nullable List<KeyFrame> positions, @Nullable List<KeyFrame> scales) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.positions = positions;
        this.scales = scales;
    }


    /**
     * Finds start and end keyframes for provided animation time.
     * <p>
     * Returns:
     * <table>
     *     <tr><th>Result</th><th>Predicate</th></tr>
     *     <tr><td>null</td><td>if frames are null</td></tr>
     *     <tr><td>Pair.of(null, firstFrame)</td><td>if time has not yet reached the first frame's one</td></tr>
     *     <tr><td>Pair.of(startFrame, endFrame)</td><td>if existing time is within these frames' time bounds</td></tr>
     *     <tr><td>Pair.of(lastFrame, null)</td><td>if existing time is behind time bounds of last frame</td></tr>
     * </table>
     *
     * @param frames       frame array in
     * @param existingTime animation's existing time
     * @return start and end keyframes for provided animation time
     */
    @Nullable//TODO check if needed for transitions
    static Pair<KeyFrame, KeyFrame> findKeyFrames(@Nullable List<KeyFrame> frames, long existingTime) {
        if (frames == null) return null;

//        if(frames.size() == 1) return new Pair<>(frames.get(0), null);

        for (int i = 0; i < frames.size(); i++) {
            KeyFrame keyFrame = frames.get(i);

            if (i == 0 && keyFrame.getTime() > existingTime) {
                return Pair.of(null, keyFrame);
            } else if (i == frames.size() - 1) {
                return Pair.of(keyFrame, null);
            }

            if (keyFrame.getTime() <= existingTime && frames.get(i + 1).getTime() > existingTime) {
                return Pair.of(keyFrame, frames.get(i + 1));
            }
        }

        return null;
    }

    @NotNull//TODO check if needed for transitions
    static Vector3f calcCurrentVectorFor(Animation animation, @NotNull Pair<KeyFrame, KeyFrame> keyPair, Vector3f defaultStartVec, int existingTime) {
        KeyFrame start = keyPair.left();
        KeyFrame end = keyPair.right();

        Vector3f startVec;
        Vector3f endVec;
        int startTime;
        int endTime;

        if (start == null) {
            startVec = defaultStartVec;
            startTime = 0;
        } else {
            startVec = start.getVec();
            startTime = start.getTime();
        }

        if (end == null) {
            endVec = startVec;
            endTime = animation.getLength();
        } else {
            endVec = end.getVec();
            endTime = end.getTime();
        }

        return interpolate(startVec, endVec, startTime, endTime, existingTime);
    }

    //TODO check if needed for transitions
    static Vector3f interpolate(Vector3f start, Vector3f end, int startTime, int endTime, int existingTime) {
        float factor = endTime - startTime == 0 ? 1 : (existingTime - startTime) / (float) (endTime - startTime);

        float outX = MathUtils.lerp(factor, start.x(), end.x());
        float outY = MathUtils.lerp(factor, start.y(), end.y());
        float outZ = MathUtils.lerp(factor, start.z(), end.z());

        return new Vector3f(outX, outY, outZ);
    }

    public void apply(Animation animation, AnimationLayer layer, TimeModelRenderer piece, int existingTime) {
        Vector3f rotateVec = KeyFrameInterpolator.calc(animation, rotations, new Vector3f(0, 0, 0), existingTime);
        if (rotateVec != null) {
            AnimationUtils.applyRotation(piece, layer, rotateVec);
        }

        Vector3f posVec = KeyFrameInterpolator.calc(animation, positions, piece.offset, existingTime);
        if (posVec != null) {
            AnimationUtils.applyOffset(piece, layer, posVec);
        }

        Vector3f currentScale = piece.getScaleFactor();
        Vector3f scaleVec = KeyFrameInterpolator.calc(animation, scales, currentScale, existingTime);
        if (scaleVec != null) {
            AnimationUtils.applyScale(piece, layer, scaleVec);
        }
    }

    public @Nullable List<KeyFrame> getPositions() {
        return positions;
    }

    public @Nullable List<KeyFrame> getRotations() {
        return rotations;
    }

    public @Nullable List<KeyFrame> getScales() {
        return scales;
    }

    public String getName() {
        return boneName;
    }
}
