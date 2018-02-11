package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jonahchin on 2017-11-14.
 */

@Entity(tableName = "ride", indices = @Index(value = "ride_id"))
public class Ride {

    @PrimaryKey
    @ColumnInfo(name = "ride_id")
    private long rideId;

    @ColumnInfo(name = "distance")
    private double distance;

    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
