
package rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ARestAPI {
    public interface IResponseHandler {
        void handle(int code, String response);

        void onError();
    }

    public ARestAPI(String address) {
        mAddress = address;
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

    protected void get(final String query, final IResponseHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection connection = null;
                try {
                    url = new URL(mAddress + query);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);

                    connection.connect();
                    int code = connection.getResponseCode();
                    if (handler != null) {
                        handler.handle(code, readInput(connection.getInputStream()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.onError();
                    }

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private String mAddress;
}
