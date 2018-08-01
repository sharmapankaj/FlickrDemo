package com.flickr.test.rest;

import android.content.Context;

import com.flickr.test.BuildConfig;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class AbstractRestClient {
    private static final long CACHE_SIZE = 10 * 1024 * 1024; //10MB
    Retrofit client;

    public AbstractRestClient(final Context context, final String baseUrl, boolean debug) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        Cache cache = new Cache(new File(context.getCacheDir(), "http"), CACHE_SIZE);
        final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.cache(cache);
        okHttpClient.readTimeout(300, TimeUnit.SECONDS);
        okHttpClient.connectTimeout(300, TimeUnit.SECONDS);

        CacheInterceptor cInterceptor = new CacheInterceptor(context);
        // okHttpClient.networkInterceptors().add(cInterceptor);
        okHttpClient.interceptors().add(cInterceptor);
        okHttpClient.interceptors().add(logging);


        client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        initApi();
    }


    public abstract void initApi();
}
