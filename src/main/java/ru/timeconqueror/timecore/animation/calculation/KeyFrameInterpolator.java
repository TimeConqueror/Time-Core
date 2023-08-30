package ru.timeconqueror.timecore.animation.calculation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrameState;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@VisibleForTesting}))
public class KeyFrameInterpolator {
    private final int animationLength;
    private final MolangEnvironment env;
    private final List<IKeyFrame> frames;
    private final int animationTime;

    @Nullable
    private IKeyFrame prev;
    @Nullable
    private IKeyFrame next;

    private int prevIndex = -1;
    private int nextIndex = -1;

    /**
     * Returns the vector, which satisfies the animation time.
     * Always return a new vector.
     */
    @Nullable
    public static Vector3f findInterpolationVec(Animation animation, MolangEnvironment env, List<IKeyFrame> frames, int animationTime) {
        if (frames.isEmpty()) return null;

        return new KeyFrameInterpolator(animation.getLength(), env, frames, animationTime).findInterpolationVec();
    }

    public static Vector3f interpolateLinear(MolangEnvironment env, IKeyFrame prev, IKeyFrame next, int animationTime) {
        return lerp(prev.getVec(env, KeyFrameState.PREV), next.getVec(env, KeyFrameState.NEXT), prev.getTime(), next.getTime(), animationTime);
    }

    private static Vector3f lerp(Vector3f start, Vector3f end, int startTime, int endTime, int animationTime) {
        float factor = MathUtils.percentage(animationTime, startTime, endTime);

        float outX = MathUtils.lerp(factor, start.x(), end.x());
        float outY = MathUtils.lerp(factor, start.y(), end.y());
        float outZ = MathUtils.lerp(factor, start.z(), end.z());

        return new Vector3f(outX, outY, outZ);
    }

    private Vector3f findInterpolationVec() {
        findKeyFramesBinSearch();

        // at this point both after and before frames are not null!
        // because findIKeyFrames sets both of them
        if (next == null) {
            //noinspection ConstantConditions
            return new Vector3f(prev.getVec(env, KeyFrameState.PREV));
        } else if (prev == null) {
            return new Vector3f(next.getVec(env, KeyFrameState.NEXT));
        }

        if (prev instanceof CatmullRomKeyFrame || next instanceof CatmullRomKeyFrame) {
            return interpolateSmoothly();
        } else {
            return interpolateLinear(env, prev, next, animationTime);
        }
    }

    @VisibleForTesting
    void findKeyFramesBinSearch() {
        if (frames.isEmpty()) return;

        int low = 0;
        int high = frames.size() - 1;

        IKeyFrame lastFrame = frames.get(high);
        if (lastFrame.getTime() < animationTime) {
            prev = lastFrame;
            prevIndex = high;
            return;
        }

        while (low <= high) {
            int mid = low + (high - low) / 2;
            IKeyFrame midFrame = frames.get(mid);

            if (midFrame.getTime() < animationTime) {
                low = mid + 1;
            } else {
                IKeyFrame prevFrame = null;
                if (mid != 0) {
                    prevFrame = frames.get(mid - 1);
                }

                if (mid == 0 || prevFrame.getTime() <= animationTime) {
                    prev = prevFrame;
                    prevIndex = mid - 1;
                    next = midFrame;
                    nextIndex = mid;
                    break;
                }

                high = mid - 1;
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

        return catmullRom(beforeMinus, prev, next, afterPlus, animationTime, animationLength);
    }

    private Vector3f catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, int existedTime, int maxTime) {
        float factor = MathUtils.percentage(existedTime, before != null ? before.getTime() : 0, after != null ? after.getTime() : maxTime);

        return catmullRom(beforeMinus, before, after, afterPlus, factor);
    }

    private Vector3f catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, float factor) {
        int allocatedSize = countNonNls(beforeMinus, before, after, afterPlus);
        Vector3f[] points = new Vector3f[allocatedSize];

        int index = 0;
        if (beforeMinus != null) points[index++] = beforeMinus.getVec(env, KeyFrameState.PREV);
        if (before != null) points[index++] = before.getVec(env, KeyFrameState.PREV);
        if (after != null) points[index++] = after.getVec(env, KeyFrameState.NEXT);
        if (afterPlus != null) points[index] = afterPlus.getVec(env, KeyFrameState.NEXT);

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
