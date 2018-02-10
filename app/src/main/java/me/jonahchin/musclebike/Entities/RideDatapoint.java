package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

/**
 * Created by jonahchin on 2018-02-09.
 */

@Entity(foreignKeys = @ForeignKey(entity = Ride.class, parentColumns = "id", childColumns = "ride_id"))
public class RideDatapoint {
    public int ride_id;

    public int left_muscle;
    public int right_muscle;
    public long elapsed_time;
    public double speed;
    public double cadence;



}
