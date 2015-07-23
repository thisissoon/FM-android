package com.soon.fm.api.http;

public enum HttpMethod {
    GET("GET");

    private final String method;

    HttpMethod(String s) {
        method = s;
    }

    public String toString() {
        return method;
    }
}
