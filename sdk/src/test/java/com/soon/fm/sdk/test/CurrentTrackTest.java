package com.soon.fm.sdk.test;

import com.soon.fm.sdk.CurrentTrack;
import com.soon.fm.sdk.entity.Album;
import com.soon.fm.sdk.entity.Artist;
import com.soon.fm.sdk.entity.Track;
import com.soon.fm.sdk.entity.User;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class CurrentTrackTest {

    @Test
    public void shouldPopulateUserData() throws Exception {
        CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm");
        User user = currentTrack.getUser();
        assertEquals("Radek Los", user.getDisplayName());
        assertEquals("Radek", user.getGivenName());
        assertEquals("Los", user.getFamilyName());
        assertEquals("e4d89664-d744-4a76-ba60-b0ba6ec92107", user.getId());
        assertEquals("https://lh6.googleusercontent.com/photo.jpg", user.getAvatarUrl());
    }

    @Test
    public void shouldPopulateElapsedTime() throws Exception {
        CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm");
        assertEquals(6000, currentTrack.getElapsedTime());
    }

    @Test
    public void shouldPopulateTrack() throws Exception {
        CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm");
        Track track = currentTrack.getTrack();
        assertEquals(track.getName(), "Dark Chest Of Wonders - Live @ Wacken 2013");
        assertEquals(track.getUri(), "spotify:track:6FshvOVICpRVkwpYE5BYTD");
        assertEquals(track.getId(), "6d40a4ba-3e4c-4012-8c79-30be1e021667");
        assertEquals(track.getDuration(), 250866);
    }

    @Test
    public void shouldPopulateTracksAlbum() throws Exception {
        CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm");
        Album track = currentTrack.getTrack().getAlbum();
        assertEquals(track.getName(), "Dark Chest Of Wonders - Live @ Wacken 2013");
        assertEquals(track.getUri(), "spotify:track:6FshvOVICpRVkwpYE5BYTD");
        assertEquals(track.getId(), "6d40a4ba-3e4c-4012-8c79-30be1e021667");
    }

    @Test
    public void shouldPopulateArtists() throws Exception {
        CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm");
        List<Artist> artists = currentTrack.getTrack().getArtists();
        assertEquals(artists.get(0).getName(), "Dark Chest Of Wonders - Live @ Wacken 2013");
        assertEquals(artists.get(0).getUri(), "spotify:track:6FshvOVICpRVkwpYE5BYTD");
        assertEquals(artists.get(0).getId(), "6d40a4ba-3e4c-4012-8c79-30be1e021667");
    }

}
