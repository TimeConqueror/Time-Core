package ru.timeconqueror.timecore.animation.calculation;

import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.List;

public class KeyFrameInterpolator {
    private final Animation animation;
    private final List<KeyFrame> frames;
    private final Vector3f defaultStartVec;
    private final int existingTime;

    private KeyFrame before;
    private KeyFrame after;

    private int beforeIndex = -1;
    private int afterIndex = -1;

    private KeyFrameInterpolator(Animation animation, List<KeyFrame> frames, Vector3f defaultStartVec, int existingTime) {
        this.animation = animation;
        this.frames = frames;
        this.defaultStartVec = defaultStartVec;
        this.existingTime = existingTime;
    }

    @Nullable
    public static Vector3f findInterpolationVec(Animation animation, List<KeyFrame> frames, Vector3f defaultStartVec, int existingTime) {
        return new KeyFrameInterpolator(animation, frames, defaultStartVec, existingTime).findInterpolationVec();
    }

    public static Vector3f interpolateLinear(Animation animation, KeyFrame before, KeyFrame after, Vector3f defaultStartVec, int existingTime) {
        Vector3f startVec;
        Vector3f endVec;
        int startTime;
        int endTime;

        if (before == null) {
            startVec = defaultStartVec;
            startTime = 0;
        } else {
            startVec = before.getVec();
            startTime = before.getTime();
        }

        if (after == null) {
            endVec = startVec;
            endTime = animation.getLength();
        } else {
            endVec = after.getVec();
            endTime = after.getTime();
        }

        return lerp(startVec, endVec, startTime, endTime, existingTime);
    }

    public static Vector3f lerp(Vector3f start, Vector3f end, int startTime, int endTime, int existingTime) {
        float factor = MathUtils.percentage(existingTime, startTime, endTime);

        float outX = MathUtils.lerp(factor, start.x(), end.x());
        float outY = MathUtils.lerp(factor, start.y(), end.y());
        float outZ = MathUtils.lerp(factor, start.z(), end.z());

        return new Vector3f(outX, outY, outZ);
    }

    @Nullable
    private Vector3f findInterpolationVec() {
        if (frames == null || frames.isEmpty()) return null;

        findKeyFrames(frames, existingTime);

        boolean smoothInterpolation = before != null && before instanceof CatmullRomKeyFrame || after != null && after instanceof CatmullRomKeyFrame;
        if (smoothInterpolation) {
            return interpolateSmoothly();
        } else {
            return interpolateLinear(animation, before, after, defaultStartVec, existingTime);
        }
    }

    private void findKeyFrames(List<KeyFrame> frames, int existingTime) {
        for (int i = 0; i < frames.size(); i++) {
            KeyFrame frame = frames.get(i);

            if (i == 0 && frame.getTime() > existingTime) {
                after = frame;
                afterIndex = i;

                break;
            } else if (i == frames.size() - 1) {
                before = frame;
                beforeIndex = i;
                break;
            }

            if (frame.getTime() <= existingTime && frames.get(i + 1).getTime() > existingTime) {
                before = frame;
                beforeIndex = i;

                afterIndex = i + 1;
                after = frames.get(afterIndex);
                break;
            }
        }
    }

    private Vector3f interpolateSmoothly() {
        KeyFrame beforeMinus = null;
        if (beforeIndex > 0) {
            beforeMinus = frames.get(beforeIndex - 1);
        }

        KeyFrame afterPlus = null;
        if (afterIndex < frames.size() - 1) {
            afterPlus = frames.get(afterIndex + 1);
        }

        return catmullRom(beforeMinus, before, after, afterPlus, existingTime, animation.getLength());
    }

    private static Vector3f catmullRom(@Nullable KeyFrame beforeMinus, @Nullable KeyFrame before, @Nullable KeyFrame after, @Nullable KeyFrame afterPlus, int existedTime, int maxTime) {
        float factor = MathUtils.percentage(existedTime, before != null ? before.getTime() : 0, after != null ? after.getTime() : maxTime);

        return catmullRom(beforeMinus, before, after, afterPlus, factor);
    }

    private static Vector3f catmullRom(@Nullable KeyFrame beforeMinus, @Nullable KeyFrame before, @Nullable KeyFrame after, @Nullable KeyFrame afterPlus, float factor) {
        int allocatedSize = countNonNls(beforeMinus, before, after, afterPlus);
        Vector3f[] points = new Vector3f[allocatedSize];

        int index = 0;
        if (beforeMinus != null) points[index++] = beforeMinus.getVec();
        if (before != null) points[index++] = before.getVec();
        if (after != null) points[index++] = after.getVec();
        if (afterPlus != null) points[index] = afterPlus.getVec();

        float time = (factor + (beforeMinus != null ? 1 : 0)) / (allocatedSize - 1);

        return catmullRom(time, points);
    }

    private static int countNonNls(@Nullable Object o1, @Nullable Object o2, @Nullable Object o3, @Nullable Object o4) {
        int count = 0;
        if (o1 != null) count++;
        if (o2 != null) count++;
        if (o3 != null) count++;
        if (o4 != null) count++;
        return count;
    }

    /**
     * https://github.com/mrdoob/three.js/blob/e48fc94dfeaecfcbfa977ba67549e6108b370cbf/src/extras/curves/SplineCurve.js#L17
     */
    private static Vector3f catmullRom(float weightIn, Vector3f[] points) {
        float p = (points.length - 1) * weightIn;
        int intPoint = (int) Math.floor(p);

        float weight = p - intPoint;

        Vector3f p0 = points[intPoint == 0 ? intPoint : intPoint - 1];
        Vector3f p1 = points[intPoint];
        Vector3f p2 = points[intPoint > points.length - 2 ? points.length - 1 : intPoint + 1];
        Vector3f p3 = points[intPoint > points.length - 3 ? points.length - 1 : intPoint + 2];

        return new Vector3f(
                catmullRom(weight, p0.x(), p1.x(), p2.x(), p3.x()),
                catmullRom(weight, p0.y(), p1.y(), p2.y(), p3.y()),
                catmullRom(weight, p0.z(), p1.z(), p2.z(), p3.z())
        );
    }

    /**
     * https://github.com/mrdoob/three.js/blob/e48fc94dfeaecfcbfa977ba67549e6108b370cbf/src/extras/core/Interpolations.js#L6
     */
    private static float catmullRom(float t, float p0, float p1, float p2, float p3) {
        float v0 = (p2 - p0) * 0.5F;
        float v1 = (p3 - p1) * 0.5F;
        float t2 = t * t;
        float t3 = t * t2;

        return (2 * p1 - 2 * p2 + v0 + v1) * t3 + (-3 * p1 + 3 * p2 - 2 * v0 - v1) * t2 + v0 * t + p1;
    }
}
