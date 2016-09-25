
package com.rpis.service.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest extends Thread {

    private HTTPRequest(String target) {
        mTarget = target;
    }

    public static HTTPResponse execute(String target) {
        HTTPRequest req = new HTTPRequest(target);

        req.start();

        try {
            req.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return req.mResponse;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(mTarget);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            connection.connect();
            int code = connection.getResponseCode();

            mResponse = new HTTPResponse(readInput(connection.getInputStream()), code);

        } catch (Exception e) {
            e.printStackTrace();
            mResponse = null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String readInput(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(
                new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }

        return total.toString();
    }

    private HTTPResponse mResponse;
    private String mTarget;
}
