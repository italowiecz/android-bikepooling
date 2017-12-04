package com.italo.bikepooling.service;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by italo on 03/12/2017.
 */

public class NetworkService {

    private GoogleMapsService googleMapsService;
    private static NetworkService networkService;

    public NetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        googleMapsService = retrofit.create(GoogleMapsService.class);
    }

    public static NetworkService getInstance(){
        if (networkService == null) {
            networkService = new NetworkService();
        }
        return networkService;
    }

    public GoogleMapsService getGoogleMapsService() {
        return googleMapsService;
    }
}
