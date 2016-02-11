package com.soon.fm;

import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.QueueItem;

import java.util.List;

public class CurrentTrackCache {

    private static CurrentTrack currentTrack = null;

    private static Boolean isMuted = null;

    private static Integer volume = null;

    private static List<QueueItem> queue = null;

    private CurrentTrackCache() {
    }

    public static CurrentTrack getCurrentTrack() {
        return currentTrack;
    }

    public static void setCurrentTrack(CurrentTrack currentTrack) {
        CurrentTrackCache.currentTrack = currentTrack;
    }

    public static Boolean getIsMuted() {
        return isMuted;
    }

    public static void setIsMuted(Boolean isMuted) {
        CurrentTrackCache.isMuted = isMuted;
    }

    public static Integer getVolume() {
        return volume;
    }

    public static void setVolume(Integer volume) {
        CurrentTrackCache.volume = volume;
    }

    public static List<QueueItem> getQueue() {
        return queue;
    }

    public static void setQueue(List<QueueItem> queue) {
        CurrentTrackCache.queue = queue;
    }

}
