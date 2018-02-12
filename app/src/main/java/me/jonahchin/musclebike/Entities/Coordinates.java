package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by jonahchin on 2018-02-11.
 */

public class Coordinates {
        @ColumnInfo(name="lat")
        public double lat;

        @ColumnInfo(name="lng")
        public double lng;
}
