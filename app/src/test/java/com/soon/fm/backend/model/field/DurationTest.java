package com.soon.fm.backend.model.field;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DurationTest {

    @Test
    public void testConversionSeconds() {
        Duration duration = new Duration(7423);
        assertEquals("00:07", duration.toString());
    }

    @Test
    public void testConversionMinutes() {
        Duration duration = new Duration(97423);
        assertEquals("01:37", duration.toString());
    }

    @Test
    public void testConversionHours() {
        Duration duration = new Duration(46049647);
        assertEquals("12:47:29", duration.toString());
    }

}
