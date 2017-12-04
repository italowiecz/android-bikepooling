package com.italo.bikepooling.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by italo on 02/12/2017.
 */

public class OverviewPolyline {

    @SerializedName("points")
    @Expose
    private String points;

    /**
     * @return The points
     */
    public String getPoints() {
        return points;
    }

    /**
     * @param points The points
     */
    public void setPoints(String points) {
        this.points = points;
    }
}
