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
    private final Class<?> responseClass;

    private HttpRequest request = null;
    private HttpCode statusCode = null;
    private String rawBody = null;

    private T body = null;

    public HttpResponse(HttpRequest<T> request, Class<?> responseClass) {
        this.request = request;
        this.responseClass = responseClass;
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
        return rawBody;
    }

    public T getBody() throws IOException, JSONException {
        if (body == null) {
            if (JSONObject.class.equals(responseClass)) {
                body = (T) asJson();
            } else if (JSONArray.class.equals(responseClass)) {
                body = (T) asJsonArray();
            } else if (String.class.equals(responseClass)) {
                body = (T) getRawBody();
            } else {
                throw new IOException("Only String and JsonObject are supported");  // TODO custom exception
            }
        }
        return body;
    }

    public JSONObject asJson() throws IOException, JSONException {
        return new JSONObject(getRawBody().trim());
    }

    public JSONArray asJsonArray() throws IOException, JSONException {
        return new JSONArray(getRawBody().trim());
    }

}
