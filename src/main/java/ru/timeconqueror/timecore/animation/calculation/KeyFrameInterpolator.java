package ru.timeconqueror.timecore.animation.calculation;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrameState;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.List;

public class KeyFrameInterpolator {
    private final Animation animation;
    private final List<IKeyFrame> frames;
    private final int existingTime;

    @Nullable
    private IKeyFrame prev;
    @Nullable
    private IKeyFrame next;

    private int prevIndex = -1;
    private int nextIndex = -1;

    private KeyFrameInterpolator(Animation animation, List<IKeyFrame> frames, int existingTime) {
        this.animation = animation;
        this.frames = frames;
        this.existingTime = existingTime;
    }

    /**
     * Returns the vector, which satisfies the animation time.
     * Always return a new vector.
     */
    @Nullable
    public static Vector3f findInterpolationVec(Animation animation, List<IKeyFrame> frames, int existingTime) {
        if (frames.isEmpty()) return null;

        return new KeyFrameInterpolator(animation, frames, existingTime).findInterpolationVec();
    }

    public static Vector3f interpolateLinear(IKeyFrame prev, IKeyFrame next, int existingTime) {
        return lerp(prev.getVec(KeyFrameState.PREV), next.getVec(KeyFrameState.NEXT), prev.getTime(), next.getTime(), existingTime);
    }

    private static Vector3f lerp(Vector3f start, Vector3f end, int startTime, int endTime, int existingTime) {
        float factor = MathUtils.percentage(existingTime, startTime, endTime);

        float outX = MathUtils.lerp(factor, start.x(), end.x());
        float outY = MathUtils.lerp(factor, start.y(), end.y());
        float outZ = MathUtils.lerp(factor, start.z(), end.z());

        return new Vector3f(outX, outY, outZ);
    }

    private Vector3f findInterpolationVec() {
        findKeyFrames(frames, existingTime);

        // at this point both after and before frames are not null!
        // because findIKeyFrames sets both of them
        if (next == null) {
            //noinspection ConstantConditions
            return new Vector3f(prev.getVec(KeyFrameState.PREV));
        } else if (prev == null) {
            return new Vector3f(next.getVec(KeyFrameState.NEXT));
        }

        if (prev instanceof CatmullRomKeyFrame || next instanceof CatmullRomKeyFrame) {
            return interpolateSmoothly();
        } else {
            return interpolateLinear(prev, next, existingTime);
        }
    }

    private void findKeyFrames(List<IKeyFrame> frames, int existingTime) {
        for (int i = 0; i < frames.size(); i++) {
            IKeyFrame frame = frames.get(i);

            if (i == 0 && frame.getTime() > existingTime) {
                next = frame;
                nextIndex = i;

                break;
            } else if (i == frames.size() - 1) {
                prev = frame;
                prevIndex = i;
                break;
            }

            if (frame.getTime() <= existingTime && frames.get(i + 1).getTime() > existingTime) {
                prev = frame;
                prevIndex = i;

                nextIndex = i + 1;
                next = frames.get(nextIndex);
                break;
            }
        }
    }

    private Vector3f interpolateSmoothly() {
        IKeyFrame beforeMinus = null;
        if (prevIndex > 0) {
            beforeMinus = frames.get(prevIndex - 1);
        }

        IKeyFrame afterPlus = null;
        if (nextIndex < frames.size() - 1) {
            afterPlus = frames.get(nextIndex + 1);
        }

        return catmullRom(beforeMinus, prev, next, afterPlus, existingTime, animation.getLength());
    }

    private static Vector3f catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, int existedTime, int maxTime) {
        float factor = MathUtils.percentage(existedTime, before != null ? before.getTime() : 0, after != null ? after.getTime() : maxTime);

        return catmullRom(beforeMinus, before, after, afterPlus, factor);
    }

    private static Vector3f catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, float factor) {
        int allocatedSize = countNonNls(beforeMinus, before, after, afterPlus);
        Vector3f[] points = new Vector3f[allocatedSize];

        int index = 0;
        if (beforeMinus != null) points[index++] = beforeMinus.getVec(KeyFrameState.PREV);
        if (before != null) points[index++] = before.getVec(KeyFrameState.PREV);
        if (after != null) points[index++] = after.getVec(KeyFrameState.NEXT);
        if (afterPlus != null) points[index] = afterPlus.getVec(KeyFrameState.NEXT);

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
