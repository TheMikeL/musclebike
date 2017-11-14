package me.jonahchin.musclebike.Entities;

import java.util.Date;

/**
 * Created by jonahchin on 2017-11-14.
 */

public class Ride {
    private long mStartTime;
    private long mEndTime;
    private Date mDate;
    private double mTotalDistance;

    public Ride(long startTime, long endTime, Date date, double totalDistance) {
        mStartTime = startTime;
        mEndTime = endTime;
        mDate = date;
        mTotalDistance = totalDistance;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public double getTotalDistance() {
        return mTotalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        mTotalDistance = totalDistance;
    }
}
