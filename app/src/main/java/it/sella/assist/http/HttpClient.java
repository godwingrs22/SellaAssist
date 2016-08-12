package it.sella.assist.http;

import android.util.Log;

import java.io.IOException;

import it.sella.assist.AppController;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class HttpClient {
    private static final String TAG = HttpClient.class.getSimpleName();
    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");

    public static String GET(HttpUrl url) throws IOException {
        Log.v(TAG, "<------Request URL----> " + url.toString());
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = AppController.getInstance().getOkHttpClient().newCall(request).execute();
        Log.v(TAG, "<------Is Response Successful----> " + response.isSuccessful());
        return response.body().string();
    }

    public static String POST(HttpUrl url, String body) throws IOException {
        Log.v(TAG, "<------Request URL----> " + url.toString());
        RequestBody requestBody = RequestBody.create(APPLICATION_JSON, body);
        Log.v(TAG, "<------RequestBody----> " + body);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = AppController.getInstance().getOkHttpClient().newCall(request).execute();
        Log.v(TAG, "<------Is Response Successful----> " + response.isSuccessful());
        return response.body().string();
    }
}