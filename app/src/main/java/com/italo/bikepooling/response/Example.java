package com.italo.bikepooling.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.italo.bikepooling.data.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by italo on 02/12/2017.
 */

public class Example {

    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();

    /**
     * @return The routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * @param routes The routes
     */
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}
