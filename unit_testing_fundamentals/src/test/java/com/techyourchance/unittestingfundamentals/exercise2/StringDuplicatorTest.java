package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {
    
    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String stringDuplicate = SUT.duplicate("");
        assertThat(stringDuplicate, is(""));
    }

    @Test
    public void duplicate_simpleString_duplicatedStringReturned() {
        String stringDuplicate = SUT.duplicate("a");
        assertThat(stringDuplicate, is("aa"));
    }

    @Test
    public void duplicate_longString_duplicatedStringReturned() {
        String stringDuplicate = SUT.duplicate("Junjie");
        assertThat(stringDuplicate, is("JunjieJunjie"));

    }
}