package me.jonahchin.musclebike.Entities;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by jonahchin on 2018-02-11.
 */

public class LinePoint {
    @ColumnInfo(name="timestamp")
    public long timestamp;

    @ColumnInfo(name="muscle")
    public float muscle;
}
