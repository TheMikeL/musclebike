package me.jonahchin.musclebike.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import me.jonahchin.musclebike.Entities.RideDatapoint;

/**
 * Created by jonahchin on 2018-02-10.
 */

@Dao
public interface RideDatapointDao {
    @Query("SELECT * FROM ride_datapoint WHERE ride_id = :rideId ORDER BY timestamp")
    List<RideDatapoint> getAllPointForRide(long rideId);

    @Query("SELECT avg(cadence) FROM ride_datapoint WHERE ride_id = :rideId")
    double getAverageCadence(long rideId);

    @Insert
    void insertAll(RideDatapoint... datapoints);
}
