package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.Timer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutoPathInstanceTest {

    @Test
    void getSegmentTime() {

        // make a 6s long path with two segements, one 2s long, one 4s long (starting at 2s)
        var autoPath = new AutoPathInstance(List.of(
                new PathPlannerTrajectory(),
                new PathPlannerTrajectory()),
                new HashMap<>(),
                List.of(
                        new AutoPathInstance.SegmentTime(0, 0),
                        new AutoPathInstance.SegmentTime(1, 2)
                ),
                AutoPath.test_forwardright,
                6);


        assertEquals(autoPath.segmentTimes.get(0), autoPath.getSegmentTime(0));
        assertEquals(autoPath.segmentTimes.get(0), autoPath.getSegmentTime(1));
        assertEquals(autoPath.segmentTimes.get(1), autoPath.getSegmentTime(2));
        assertEquals(autoPath.segmentTimes.get(1), autoPath.getSegmentTime(5));
    }

    @Test
    void sampleHasEventStarted() {
        var autoPath = new AutoPathInstance(List.of(
                new PathPlannerTrajectory(),
                new PathPlannerTrajectory()
        ),
                Map.of(
                        "event1", 1.,
                        "event2", 3.
                ),
                List.of(
                        new AutoPathInstance.SegmentTime(0, 0),
                        new AutoPathInstance.SegmentTime(1, 2)
                ),
                AutoPath.test_forwardright,
                6);

        var timer = mock(Timer.class);
        autoPath.pathTimer = timer;

        when(timer.get()).thenReturn(0d);
        assertFalse(autoPath.sampleHasEventStarted("event1"));
        when(timer.get()).thenReturn(1d);
        assertTrue(autoPath.sampleHasEventStarted("event1"));
        when(timer.get()).thenReturn(4d);
        assertTrue(autoPath.sampleHasEventStarted("event2"));

    }

    @Test
    void sampleSpeeds() {
    }
}