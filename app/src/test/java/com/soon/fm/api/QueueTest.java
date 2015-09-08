package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;
import com.soon.fm.api.model.Artist;
import com.soon.fm.api.model.QueueItem;
import com.soon.fm.api.model.Track;
import com.soon.fm.api.model.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueueTest {

    private BufferedReader in;

    @Before
    public void setup() throws Exception {
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/queue.json")));
    }

    @After
    public void teardown() throws Exception {
        if (in != null) {
            in.close();
        }
        in = null;
    }

    @Test
    public void testGetTracks() throws Exception {
        HttpResponse<JSONObject> mockHttpResponse = (HttpResponse<JSONObject>) mock(HttpResponse.class);
        when(mockHttpResponse.asJsonArray()).thenReturn(new JSONArray(in.readLine()));

        Queue queue = new Queue("http://example.com");
        queue.setJsonResponse(mockHttpResponse);

        QueueItem item = queue.getTracks().get(0);

        Track track = item.getTrack();
        assertEquals("7a394d20-31fb-4da3-abef-44b0eeb40136", track.getId());
        assertEquals("spotify:track:22AHJvVaodJrwkJFpMITU6", track.getUri());
        assertEquals("So Here We Are", track.getName());
        assertEquals(232893, track.getDuration().getMillis());
        assertEquals(1, track.getArtists().size());

        Artist artist = track.getArtists().get(0);
        assertEquals("86a41013-5df1-475a-89a1-82a6415b9d9c", artist.getId());
        assertEquals("spotify:artist:3MM8mtgFzaEJsqbjZBSsHJ", artist.getUri());
        assertEquals("Bloc Party", artist.getName());

        User user = item.getUser();
        assertEquals("5db00514-2628-4017-99c1-01ffc601e696", user.getId());
        assertEquals("Florence Holmes", user.getDisplayName());
        assertEquals("Holmes", user.getFamilyName());
        assertEquals("Florence", user.getGivenName());
        assertEquals("https://lh3.googleusercontent.com/-zijJ8z7XR-8/AAAAAAAAAAI/AAAAAAAAACk/WPtByWe1SlU/photo.jpg", user.getAvatar().getUrl());

    }
}
