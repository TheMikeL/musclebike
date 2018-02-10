package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by jonahchin on 2017-11-14.
 */

@Entity(tableName = "ride")
public class Ride {

    @PrimaryKey
    @ColumnInfo(name = "ride_id")
    private long rideId;

    @ColumnInfo(name = "distance")
    private double distance;

    @ColumnInfo(name = "elapsed_time")
    private String elapsedTime;

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long ride_id) {
        this.rideId = ride_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
