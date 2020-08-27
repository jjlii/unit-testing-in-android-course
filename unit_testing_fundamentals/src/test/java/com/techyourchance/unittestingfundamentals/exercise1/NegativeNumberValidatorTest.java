package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NegativeNumberValidatorTest {

    NegativeNumberValidator SUT;

    @Before
    public void setUp(){
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void isNegative_negativeNumber_trueReturned(){
        boolean isNegative = SUT.isNegative(-1);
        assertThat(isNegative, is(true));
    }


    @Test
    public void isNegative_zero_falseReturned(){
        boolean isNegative = SUT.isNegative(0);
        assertThat(isNegative, is(false));
    }


    @Test
    public void isNegative_positiveNumber_falseReturned(){
        boolean isNegative = SUT.isNegative(1);
        assertThat(isNegative, is(false));
    }



}