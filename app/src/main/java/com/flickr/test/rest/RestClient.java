package com.flickr.test.rest;

import android.content.Context;

import com.flickr.test.restservice.RestService;


public class RestClient extends AbstractRestClient {

    public RestService mRestService;

    public RestClient(Context context, String baseUrl, boolean debug) {
        super(context, baseUrl, debug);
    }


    @Override
    public void initApi() {

        mRestService = client.create(RestService.class);

    }


    public RestService getRestService() {
        return mRestService;
    }


}
