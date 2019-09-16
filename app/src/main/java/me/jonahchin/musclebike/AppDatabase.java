package me.jonahchin.musclebike;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.Entities.RideDatapoint;
import me.jonahchin.musclebike.Interfaces.RideDao;
import me.jonahchin.musclebike.Interfaces.RideDatapointDao;

/**
 * Created by jonahchin on 2018-02-10.
 */

@Database(entities = {Ride.class, RideDatapoint.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public abstract RideDao rideDao();
    public abstract RideDatapointDao rideDatapointDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
