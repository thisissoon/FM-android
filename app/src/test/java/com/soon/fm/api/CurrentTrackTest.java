package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrentTrackTest {

    private BufferedReader in;

    @Before
    public void setUp() throws IOException {
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/current_track.json")));
    }

    @After
    public void tearDown() throws IOException {
        if (in != null) {
            in.close();
        }
        in = null;
    }
//
//    @Test
//    public void buildUrl() throws MalformedURLException {
//        CurrentTrack currentTrack = new CurrentTrack("http://example.com");
//        assertEquals("http://example.com/player/current", currentTrack.getUrl().toString());
//    }
//
//    @Test
//    public void buildUrlWithBackSlash() throws MalformedURLException {
//        CurrentTrack currentTrack = new CurrentTrack("http://example.com/");
//        assertEquals("http://example.com/player/current", currentTrack.getUrl().toString());
//    }
//
//    @Test
//    public void trackShouldBePopulated() throws Exception {
//        CurrentTrack currentTrack = mock(CurrentTrack.class);
//        when(currentTrack.getPayload(JSONObject.class)).thenReturn(new JSONObject(in.readLine()));
//
//        assertNotNull("Track should not be null", currentTrack.getTrack());
//        assertEquals("???", currentTrack.getTrack().getName());
//    }

    @Test
    public void elapsedTimeShouldBePopulated() throws Exception {
        HttpResponse<JSONObject> mockHttpResponse = (HttpResponse<JSONObject>) mock(HttpResponse.class);
        when(mockHttpResponse.getContent()).thenReturn("fldsjfls ajflsjf lsdkjf");

        assertNull(in.readLine());

        assertEquals("", mockHttpResponse.getContent());
//        CurrentTrack mockCurrentTrack = mock(CurrentTrack.class);
//        when(currentTrack.getResponse()).thenReturn(new JSONObject(in.readLine()));
//
//        assertEquals(1000, currentTrack.getPayload(JSONObject.class).getJSONObject("player").getInt("elapsed_time"));
//        assertNotNull("Track should not be null", currentTrack.getElapsedTime());
    }
}
