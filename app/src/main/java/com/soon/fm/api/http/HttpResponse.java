package com.soon.fm.api.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private String callRequest() throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) request.getUrl().openConnection();
            conn.setReadTimeout(TIMEOUT_MILLIS);
            conn.setConnectTimeout(TIMEOUT_MILLIS1);
            conn.setRequestMethod(request.getMethod().toString());
            conn.setDoInput(true);
            conn.connect();

            setStatusCode(conn.getResponseCode());
            is = conn.getInputStream();
            return convertInputStreamToString(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertInputStreamToString(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
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
            rawBody = callRequest();
        }
        return rawBody;
    }

    public T getBody() throws Exception {
        if (body == null) {
            if (JSONObject.class.equals(responseClass)) {
                body = (T) asJson();
            } else if (String.class.equals(responseClass)) {
                body = (T) getRawBody();
            } else {
                throw new Exception("Only String and JsonObject are supported");  // TODO custom exception
            }
        }
        return body;
    }

    public JSONObject asJson() throws IOException, JSONException {
        return new JSONObject(getRawBody().trim());
    }

}
