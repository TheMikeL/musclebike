package me.jonahchin.musclebike;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.Interfaces.RideDao;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jonahchin on 2018-02-10.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {

    private RideDao mRideDao;
    private AppDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mRideDao = mDb.rideDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        Ride ride = new Ride();
        ride.setRideId(10);
        ride.setElapsedTime("5h5m");
        ride.setDistance(10.2);
        mRideDao.insertAll(ride);

        Ride result = mRideDao.findById(10);
        assertThat(result.getDistance(), equalTo(10.2));
    }
}
