package com.soon.fm.api.model.field;


public class Duration {

    private int millis;

    public Duration(int ms) {
        millis = ms;
    }

    @Override
    public String toString(){
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

}
