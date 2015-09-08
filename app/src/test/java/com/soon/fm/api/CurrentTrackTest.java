package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;
import com.soon.fm.api.model.Artist;
import com.soon.fm.api.model.Track;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrentTrackTest {

    private BufferedReader in;

    @Before
    public void setup() throws Exception {
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/current_track.json")));
    }

    @After
    public void teardown() throws Exception {
        if (in != null) {
            in.close();
        }
        in = null;
    }

    @Test
    public void buildUrl() throws MalformedURLException {
        CurrentTrack currentTrack = new CurrentTrack("http://example.com");
        assertEquals("http://example.com/player/current", currentTrack.getUrl().toString());
    }

    @Test
    public void buildUrlWithBackSlash() throws MalformedURLException {
        CurrentTrack currentTrack = new CurrentTrack("http://example.com/");
        assertEquals("http://example.com/player/current", currentTrack.getUrl().toString());
    }

    @Test
    public void trackShouldBePopulated() throws Exception {
        HttpResponse<JSONObject> mockHttpResponse = (HttpResponse<JSONObject>) mock(HttpResponse.class);
        when(mockHttpResponse.asJson()).thenReturn(new JSONObject(in.readLine()));

        CurrentTrack currentTrack = new CurrentTrack("http://example.com");
        currentTrack.setJsonResponse(mockHttpResponse);

        Track track = currentTrack.getTrack();
        assertEquals("2c16ec0c-8a69-4ac9-ad0a-82c402c45856", track.getId());
        assertEquals("spotify:track:7xqK9Kc2m6Nkn8Settuy16", track.getUri());
        assertEquals("Miami 2 Ibiza - (Swedish House Mafia vs. Tinie Tempah)", track.getName());
        assertEquals(206460, track.getDuration().getMillis());
        assertEquals(6, track.getArtists().size());

        assertEquals("6a4b84f1-82c2-4c33-8751-087f8d5a854f", track.getAlbum().getId());
        assertEquals("Disc-Overy", track.getAlbum().getName());
        assertEquals("spotify:album:0B0XOuBWbgLAOkmOFXDe9M", track.getAlbum().getUri());
        assertEquals(3, track.getAlbum().getImages().size());

        Artist artist = track.getArtists().get(0);
        assertEquals("e8e01d19-016c-4e08-a3d7-c00486265593", artist.getId());
        assertEquals("Swedish House Mafia", artist.getName());
        assertEquals("spotify:artist:1h6Cn3P4NGzXbaXidqURXs", artist.getUri());
    }

    @Test
    public void elapsedTimeShouldBePopulated() throws Exception {
        HttpResponse<JSONObject> mockHttpResponse = (HttpResponse<JSONObject>) mock(HttpResponse.class);
        when(mockHttpResponse.asJson()).thenReturn(new JSONObject(in.readLine()));

        CurrentTrack currentTrack = new CurrentTrack("http://example.com");
        currentTrack.setJsonResponse(mockHttpResponse);

        assertEquals(169096, currentTrack.getElapsedTime().getMillis());
    }

}
