package com.soon.fm.api.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpResponse<T> {

    private static final int TIMEOUT_MILLIS = 10000;
    private static final int TIMEOUT_MILLIS1 = 15000;

    private HttpRequest request = null;
    private HttpCode statusCode = null;
    private String rawBody = null;

    public HttpResponse(HttpRequest<T> request) {
        this.request = request;
    }

    public String getContent() throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection conn = request.createHttpURLConnection();
            conn.setReadTimeout(TIMEOUT_MILLIS);
            conn.setConnectTimeout(TIMEOUT_MILLIS1);
            conn.setRequestMethod(request.getMethod().toString());
            conn.setDoInput(true);
            conn.connect();

            setStatusCode(conn.getResponseCode());
            InputStream stream = conn.getInputStream();
            return convertInputStreamToString(stream);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertInputStreamToString(InputStream stream) throws IOException {
        final StringBuffer content = new StringBuffer();
        int count;
        while (-1 != (count = stream.read())) {
            content.append(new String(Character.toChars(count)));
        }
        return content.toString();
    }

    private void setStatusCode(int statusCode) {
        switch (statusCode) {
            case 200:
                this.statusCode = HttpCode.OK;
                break;
        }
    }

    public HttpCode getHttpStatus() {
        return this.statusCode;
    }

    public String getRawBody() throws IOException {
        if (rawBody == null) {
            rawBody = getContent();
        }
        return getContent();
    }

    public JSONObject asJson() throws IOException, JSONException {
        return new JSONObject(getRawBody().trim());
    }

    public JSONArray asJsonArray() throws IOException, JSONException {
        return new JSONArray(getRawBody().trim());
    }

}
