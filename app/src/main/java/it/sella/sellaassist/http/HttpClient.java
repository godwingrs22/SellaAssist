package it.sella.sellaassist.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class HttpClient {
    private static final String TAG = HttpClient.class.getSimpleName();
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final int TIMEOUT = 5000;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    InputStream inputStream = null;

    public String getResponse(final URL url, final String method, final String input, final int timeout) {
        String response = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            Log.v(TAG, "<------Request URL----> " + url);
            if (timeout != 0) {
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
            }

            if (HTTP_POST.equals(method) && input != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(input.getBytes("UTF-8"));
                os.close();
            }

            urlConnection.connect();
            Log.v(TAG, "<------Response Code----> " + urlConnection.getResponseCode());
            inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            response = buffer.toString();

        } catch (IOException exception) {
            Log.e(TAG, "Error ", exception);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.v(TAG, "<------Http Response----> " + response);
        return response;
    }
}
