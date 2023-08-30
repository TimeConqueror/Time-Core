package ru.timeconqueror.timecore.animation.calculation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.timeconqueror.timecore.animation.component.ConstantVector;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedField;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class KeyFrameInterpolatorTest {
    private final UnlockedField<KeyFrameInterpolator, IKeyFrame> prev = ReflectionHelper.findField(KeyFrameInterpolator.class, "prev");
    private final UnlockedField<KeyFrameInterpolator, IKeyFrame> next = ReflectionHelper.findField(KeyFrameInterpolator.class, "next");
    private final UnlockedField<KeyFrameInterpolator, Integer> prevIndex = ReflectionHelper.findField(KeyFrameInterpolator.class, "prevIndex");
    private final UnlockedField<KeyFrameInterpolator, Integer> nextIndex = ReflectionHelper.findField(KeyFrameInterpolator.class, "nextIndex");

    private MolangEnvironment env;

    private static KeyFrame frame(int time) {
        return new KeyFrame(time, new ConstantVector(new Vector3f()));
    }

    @BeforeEach
    public void setup() {
        env = Mockito.mock(MolangEnvironment.class);
    }

    @Test
    public void testSearchWhenNoFrames() {
        var interpol = new KeyFrameInterpolator(1000, env, List.of(), 500);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertNull(next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(-1, nextIndex(interpol));
    }

    @Test
    public void testSearchBetweenTwo() {
        KeyFrame frame = frame(0);
        KeyFrame frame1 = frame(1000);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1), 500);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame, prev(interpol));
        assertEquals(frame1, next(interpol));
        assertEquals(0, prevIndex(interpol));
        assertEquals(1, nextIndex(interpol));
    }

    @Test
    public void testSearchBetweenThree() {
        KeyFrame frame = frame(0);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(1000);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 600);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame1, prev(interpol));
        assertEquals(frame2, next(interpol));
        assertEquals(1, prevIndex(interpol));
        assertEquals(2, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenHigherThenBothTwoHold() {
        KeyFrame frame = frame(0);
        KeyFrame frame1 = frame(500);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1), 1000);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame1, prev(interpol));
        assertNull(next(interpol));
        assertEquals(1, prevIndex(interpol));
        assertEquals(-1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenHigherThenBothThreeHold() {
        KeyFrame frame = frame(0);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(900);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 1000);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame2, prev(interpol));
        assertNull(next(interpol));
        assertEquals(2, prevIndex(interpol));
        assertEquals(-1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenHigherThenSingleHold() {
        KeyFrame frame = frame(0);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame), 1000);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame, prev(interpol));
        assertNull(next(interpol));
        assertEquals(0, prevIndex(interpol));
        assertEquals(-1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenLowerThenBothTwoHold() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1), 50);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertEquals(frame, next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(0, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenLowerThenBothThreeHold() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(900);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 50);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertEquals(frame, next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(0, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenLowerThenSingleHold() {
        KeyFrame frame = frame(100);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame), 50);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertEquals(frame, next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(0, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToFirstOfTwo() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1), 100);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertEquals(frame, next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(0, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToSecondOfTwo() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1), 500);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame, prev(interpol));
        assertEquals(frame1, next(interpol));
        assertEquals(0, prevIndex(interpol));
        assertEquals(1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToFirstOfThree() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(900);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 100);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame, prev(interpol));
        assertEquals(frame1, next(interpol));
        assertEquals(0, prevIndex(interpol));
        assertEquals(1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToSecondOfThree() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(900);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 500);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame, prev(interpol));
        assertEquals(frame1, next(interpol));
        assertEquals(0, prevIndex(interpol));
        assertEquals(1, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToThirdOfThree() {
        KeyFrame frame = frame(100);
        KeyFrame frame1 = frame(500);
        KeyFrame frame2 = frame(900);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame, frame1, frame2), 900);
        interpol.findKeyFramesBinSearch();

        assertEquals(frame1, prev(interpol));
        assertEquals(frame2, next(interpol));
        assertEquals(1, prevIndex(interpol));
        assertEquals(2, nextIndex(interpol));
    }

    @Test
    public void testSearchWhenEqualsToSingle() {
        KeyFrame frame = frame(100);
        var interpol = new KeyFrameInterpolator(1000, env, List.of(frame), 100);
        interpol.findKeyFramesBinSearch();

        assertNull(prev(interpol));
        assertEquals(frame, next(interpol));
        assertEquals(-1, prevIndex(interpol));
        assertEquals(0, nextIndex(interpol));
    }

    private IKeyFrame prev(KeyFrameInterpolator interpolator) {
        return prev.get(interpolator);
    }

    private IKeyFrame next(KeyFrameInterpolator interpolator) {
        return next.get(interpolator);
    }

    private Integer prevIndex(KeyFrameInterpolator interpolator) {
        return prevIndex.get(interpolator);
    }

    private Integer nextIndex(KeyFrameInterpolator interpolator) {
        return nextIndex.get(interpolator);
    }
}
