package com.flickr.test.restservice;


import com.flickr.test.models.PublicData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestService {

    @GET("feeds/photos_public.gne")
    Call<PublicData> getPublicData(@Query("format") String format, @Query("nojsoncallback") int nojsoncallback, @Query("page") int page);

}
