package me.jonahchin.musclebike.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.jonahchin.musclebike.Entities.Ride;

/**
 * Created by jonahchin on 2018-02-10.
 */

@Dao
public interface RideDao {

    @Query("SELECT * FROM ride")
    List<Ride> getAll();

    @Query("SELECT * FROM ride WHERE ride_id = :rideId")
    Ride findById(long rideId);

    @Insert
    void insertAll(Ride... rides);

    @Delete
    void delete(Ride ride);

    @Query("DELETE FROM ride")
    void nukeTable();

    @Update
    void updateRides(Ride... rides);

}
