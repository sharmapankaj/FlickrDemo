package com.flickr.test;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.flickr.test.rest.RestClient;


public class FlickrApplication extends Application {

    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;
    public static RestClient restClient;
    private static FlickrApplication instance = null;
    private static final String TESTING_URL = "https://api.flickr.com/services/";

    public synchronized static FlickrApplication getInstance() {
        return instance;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        restClient = new RestClient(getApplicationContext(), TESTING_URL, true);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        Log.i("AndroidApplication",
                String.format("Screen size: w = %d, h = %d", SCREEN_WIDTH, SCREEN_HEIGHT));
    }

}
