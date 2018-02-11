package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by jonahchin on 2018-02-09.
 */

@Entity(tableName = "ride_datapoint", foreignKeys = @ForeignKey(entity = Ride.class, parentColumns = "ride_id", childColumns = "ride_id", onDelete = CASCADE))
public class RideDatapoint {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "ride_id")
    private long rideId;

    private long timestamp;
    private double cadence;
    private double muscle;
    private int balance;
    private double lat;
    private double lng;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getCadence() {
        return cadence;
    }

    public void setCadence(double cadence) {
        this.cadence = cadence;
    }

    public double getMuscle() {
        return muscle;
    }

    public void setMuscle(double muscle) {
        this.muscle = muscle;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
