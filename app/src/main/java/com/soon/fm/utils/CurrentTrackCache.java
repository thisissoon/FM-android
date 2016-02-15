package com.soon.fm.utils;

import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.QueueItem;

import java.util.List;

public class CurrentTrackCache {

    private static int bitmask = 0x0000;

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
        bitmask |= 0x0001;
        CurrentTrackCache.currentTrack = currentTrack;
    }

    public static Boolean getIsMuted() {
        return isMuted;
    }

    public static void setIsMuted(Boolean isMuted) {
        bitmask |= 0x0010;
        CurrentTrackCache.isMuted = isMuted;
    }

    public static Integer getVolume() {
        return volume;
    }

    public static void setVolume(Integer volume) {
        bitmask |= 0x0100;
        CurrentTrackCache.volume = volume;
    }

    public static List<QueueItem> getQueue() {
        return queue;
    }

    public static void setQueue(List<QueueItem> queue) {
        bitmask |= 0x1000;
        CurrentTrackCache.queue = queue;
    }

    public static boolean isEverythingSet() {
        return bitmask == 0x1111;
    }

}
