package me.jonahchin.musclebike.Interfaces;

import me.jonahchin.musclebike.Entities.Ride;

/**
 * Created by jonahchin on 2017-11-22.
 */

public interface HistoryListCallbacks {
    void onListItemClick(Ride ride, boolean backStack);
}
