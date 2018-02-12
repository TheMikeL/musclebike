package me.jonahchin.musclebike.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.util.Pair;

import java.util.List;

import me.jonahchin.musclebike.Entities.Coordinates;
import me.jonahchin.musclebike.Entities.LinePoint;
import me.jonahchin.musclebike.Entities.RideDatapoint;

/**
 * Created by jonahchin on 2018-02-10.
 */

@Dao
public interface RideDatapointDao {
    @Query("SELECT * FROM ride_datapoint WHERE ride_id = :rideId ORDER BY timestamp")
    List<RideDatapoint> getAllPointForRide(long rideId);

    @Query("SELECT timestamp, muscle FROM ride_datapoint WHERE ride_id = :rideId ORDER BY timestamp")
    List<LinePoint> getChartData(long rideId);

    @Query("SELECT avg(cadence) FROM ride_datapoint WHERE ride_id = :rideId")
    double getAverageCadence(long rideId);

    @Query("SELECT avg(balance) FROM ride_datapoint WHERE ride_id = :rideId")
    float getAverageBalance(long rideId);

    @Query("SELECT count(muscle) FROM ride_datapoint WHERE ride_id = :rideId AND muscle < 31 ")
    int getNumLowIntensities(long rideId);

    @Query("SELECT count(muscle) FROM ride_datapoint WHERE ride_id = :rideId AND muscle BETWEEN 31 AND 70 ")
    int getNumMedIntensities(long rideId);

    @Query("SELECT count(muscle) FROM ride_datapoint WHERE ride_id = :rideId AND muscle > 70 ")
    int getNumHighIntensities(long rideId);

    @Query("SELECT min(lat) FROM ride_datapoint WHERE ride_id = :rideId")
    double getMinLat(long rideId);

    @Query("SELECT min(lng) FROM ride_datapoint WHERE ride_id = :rideId")
    double getMinLong(long rideId);

    @Query("SELECT max(lat) FROM ride_datapoint WHERE ride_id = :rideId")
    double getMaxLat(long rideId);

    @Query("SELECT max(lng) FROM ride_datapoint WHERE ride_id = :rideId")
    double getMaxLong(long rideId);

    @Query("SELECT lat, lng FROM ride_datapoint WHERE ride_id = :rideId ORDER BY timestamp")
    List<Coordinates> getAllCoordinates(long rideId);

    @Insert
    void insertAll(RideDatapoint... datapoints);
}
