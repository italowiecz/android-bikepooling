package com.italo.bikepooling.service;

import com.italo.bikepooling.response.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by italo on 02/12/2017.
 */

public interface GoogleMapsService {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json?key=AIzaSyC22GfkHu9FdgT9SwdCWMwKX1a4aohGifM")
    Call<Example> getDistanceDuration(@Query("units") String units,
                                      @Query("origin") String origin,
                                      @Query("destination") String destination,
                                      @Query("mode") String mode);

}
