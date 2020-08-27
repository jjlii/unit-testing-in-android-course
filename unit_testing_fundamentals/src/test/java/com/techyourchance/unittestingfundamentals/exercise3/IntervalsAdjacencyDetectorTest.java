package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void intervalsAdjacency_notAdjacencyIntervals_falseReturned() {
        Interval interval1 = new Interval(-2, 5);
        Interval interval2 = new Interval(6, 10);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(false));
    }

    @Test
    public void intervalsAdjacency_sameInterval_falseReturned() {
        Interval interval1 = new Interval(-2, 5);
        Interval interval2 = new Interval(-2, 5);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(false));
    }

    @Test
    public void intervalsAdjacency_interval1AdjacencyInterval2_trueReturned() {
        Interval interval1 = new Interval(-2, 6);
        Interval interval2 = new Interval(6, 10);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(true));
    }


    @Test
    public void intervalsAdjacency_interval1OverlapInterval2_falseReturned() {
        Interval interval1 = new Interval(-2, 8);
        Interval interval2 = new Interval(7, 10);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(false));
    }

    @Test
    public void intervalsAdjacency_interval1AndInterval2SameStartAndEndPoint_trueReturned() {
        Interval interval1 = new Interval(-2, 0);
        Interval interval2 = new Interval(-2, 0);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(false));
    }

    @Test
    public void intervalsAdjacency_interval1AfterInterval2SameStartAndEndPoint_trueReturned() {
        Interval interval1 = new Interval(-2, 5);
        Interval interval2 = new Interval(-5, -3);
        boolean intervalsAdjacency = SUT.isAdjacent(interval1, interval2);
        assertThat(intervalsAdjacency, is(false));
    }


}