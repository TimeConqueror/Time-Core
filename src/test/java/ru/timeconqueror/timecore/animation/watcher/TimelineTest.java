package ru.timeconqueror.timecore.animation.watcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class TimelineTest {
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void getElapsedLength_ShouldHaveSameLengthWhenDefaultValues(int length) {
        var timeline = new Timeline(length, 1.0F, false, 0, 0);
        assertEquals(length, timeline.getElapsedLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void getElapsedLength_ShouldBeTwiceLongerDueToSpeed(int length) {
        var timeline = new Timeline(length, 0.5F, false, 0, 0);
        assertEquals(length * 2, timeline.getElapsedLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    public void getElapsedLength_ShouldBeTwiceShorterDueToSpeed(int length) {
        var timeline = new Timeline(length, 2F, false, 0, 0);
        assertEquals(length / 2, timeline.getElapsedLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void getElapsedLength_ShouldBeSameIfJustReversed(int length) {
        var timeline = new Timeline(length, 1F, true, 0, length);
        assertEquals(length, timeline.getElapsedLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void getElapsedLength_ShouldBeMaxIfSpeedIsZero(int length) {
        var timeline = new Timeline(length, 0, false, 0, 0);
        assertEquals(Integer.MAX_VALUE, timeline.getElapsedLength());

        timeline = new Timeline(length, 0, true, 0, 0);
        assertEquals(Integer.MAX_VALUE, timeline.getElapsedLength());

        timeline = new Timeline(length, 0, false, 0, length);
        assertEquals(Integer.MAX_VALUE, timeline.getElapsedLength());

        timeline = new Timeline(length, 0, true, 0, length);
        assertEquals(Integer.MAX_VALUE, timeline.getElapsedLength());
    }

    @Test
    public void getElapsedLength_ShouldConsiderStartAnimationTime() {
        Timeline timeline;

        // not reversed
        timeline = new Timeline(1000, 1, false, 0, 1000);
        assertEquals(0, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, false, 0, 500);
        assertEquals(500, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, false, 0, 250);
        assertEquals(750, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, false, 0, 750);
        assertEquals(250, timeline.getElapsedLength());

        //reversed (animationTimeStartFrom should represent the time in non-reversed version of animation)
        timeline = new Timeline(1000, 1, true, 0, 1000);
        assertEquals(1000, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, true, 0, 0);
        assertEquals(0, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, true, 0, 500);
        assertEquals(500, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, true, 0, 750);
        assertEquals(750, timeline.getElapsedLength());
        timeline = new Timeline(1000, 1, true, 0, 250);
        assertEquals(250, timeline.getElapsedLength());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1000, 2000})
    public void getElapsedTime_ShouldBeRightAndWithinBorders(int length) {
        var timeline = spy(new Timeline(length, 1, false, 0, 0));
        doReturn(length).when(timeline).getElapsedLength();

        assertEquals(0, timeline.getElapsedTime(-1));
        // start of the animation
        assertEquals(0, timeline.getElapsedTime(0));
        // middle of the animation
        assertEquals(length / 2, timeline.getElapsedTime(length / 2));
        // end of animation
        assertEquals(length, timeline.getElapsedTime(length));
        // time right after animation end
        assertEquals(length, timeline.getElapsedTime(length + 1));
        // time too far from animation end
        assertEquals(length, timeline.getElapsedTime(Integer.MAX_VALUE));
    }

    @Test
    public void getAnimationTime_ShouldBeInBoundsAndRightForDefaultValues() {
        var timeline = new Timeline(1000, 1, false, 0, 0);

        assertEquals(0, timeline.getAnimationTime(-1));
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(250, timeline.getAnimationTime(250));
        assertEquals(500, timeline.getAnimationTime(500));
        assertEquals(750, timeline.getAnimationTime(750));
        assertEquals(1000, timeline.getAnimationTime(1000));
        assertEquals(1000, timeline.getAnimationTime(1001));
        assertEquals(1000, timeline.getAnimationTime(Integer.MAX_VALUE));
    }

    @Test
    public void getAnimationTime_ShouldBeRightForVariousSpeed() {
        int length = 1000;
        // stay in start if speed is zero
        var timeline = new Timeline(length, 0, false, 0, 0);
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(0, timeline.getAnimationTime(500));
        assertEquals(0, timeline.getAnimationTime(1000));

        // twice shorter
        timeline = new Timeline(length, 0.5F, false, 0, 0);
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(1000));
        assertEquals(1000, timeline.getAnimationTime(2000));

        // normal speed
        timeline = new Timeline(length, 1, false, 0, 0);
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(500));
        assertEquals(1000, timeline.getAnimationTime(1000));

        // twice faster
        timeline = new Timeline(length, 2, false, 0, 0);
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(250));
        assertEquals(1000, timeline.getAnimationTime(500));

        // #### REVERSED variants ####

        // stay in end if speed is zero
        timeline = new Timeline(length, 0, true, 0, length);
        assertEquals(1000, timeline.getAnimationTime(0));
        assertEquals(1000, timeline.getAnimationTime(500));
        assertEquals(1000, timeline.getAnimationTime(1000));

        // twice shorter
        timeline = new Timeline(length, 0.5F, true, 0, length);
        assertEquals(1000, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(1000));
        assertEquals(0, timeline.getAnimationTime(2000));

        // normal speed
        timeline = new Timeline(length, 1, true, 0, length);
        assertEquals(1000, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(500));
        assertEquals(0, timeline.getAnimationTime(1000));

        // twice faster
        timeline = new Timeline(length, 2, true, 0, length);
        assertEquals(1000, timeline.getAnimationTime(0));
        assertEquals(500, timeline.getAnimationTime(250));
        assertEquals(0, timeline.getAnimationTime(500));
    }

    @Test
    public void getAnimationTime_ShouldBeRightForVariousAnimationStartTime() {
        // from middle to end
        var timeline = new Timeline(1000, 1, false, 0, 500);
        assertEquals(500, timeline.getAnimationTime(0));
        assertEquals(750, timeline.getAnimationTime(250));
        assertEquals(1000, timeline.getAnimationTime(500));

        // from last quarter to end
        timeline = new Timeline(1000, 1, false, 0, 750);
        assertEquals(750, timeline.getAnimationTime(0));
        assertEquals(750 + 125, timeline.getAnimationTime(125));
        assertEquals(1000, timeline.getAnimationTime(250));

        // from middle to start
        timeline = new Timeline(1000, 1, true, 0, 500);
        assertEquals(500, timeline.getAnimationTime(0));
        assertEquals(250, timeline.getAnimationTime(250));
        assertEquals(0, timeline.getAnimationTime(500));

        // from last quarter to start
        timeline = new Timeline(1000, 1, true, 0, 750);
        assertEquals(750, timeline.getAnimationTime(0));
        assertEquals(250, timeline.getAnimationTime(500));
        assertEquals(0, timeline.getAnimationTime(750));

        // stay at end
        timeline = new Timeline(1000, 1, false, 0, 1000);
        assertEquals(1000, timeline.getAnimationTime(0));
        assertEquals(1000, timeline.getAnimationTime(500));
        assertEquals(1000, timeline.getAnimationTime(1000));

        // stay at start
        timeline = new Timeline(1000, 1, true, 0, 0);
        assertEquals(0, timeline.getAnimationTime(0));
        assertEquals(0, timeline.getAnimationTime(500));
        assertEquals(0, timeline.getAnimationTime(1000));
    }
}
