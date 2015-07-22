package com.soon.fm.api.http;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest<T> {

    private final URL url;
    private final HttpMethod method;
    private Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

    public HttpRequest(HttpMethod method, URL url) {
        this.method = method;
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequest header(String name, String value) {
        List<String> list = this.headers.get(name.trim());
        if (list == null) {
            list = new ArrayList<String>();
        }
        list.add(value);
        this.headers.put(name.trim(), list);
        return this;
    }

    public HttpRequest basicAuth(String username, String password) {
        // header("Authorization", "Basic " + Base64Coder.encodeString(username+ ":" + password));
        return this;
    }

    public HttpResponse<T> call() {
        return new HttpResponse<T>(this, JSONObject.class);
    }
}
