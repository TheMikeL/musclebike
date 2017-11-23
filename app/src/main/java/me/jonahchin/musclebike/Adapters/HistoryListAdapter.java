package me.jonahchin.musclebike.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.R;

/**
 * Created by jonahchin on 2017-11-14.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.RideHolder>{

    private List<Ride> rides;

    public HistoryListAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @Override
    public RideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);
        return new RideHolder(view);
    }

    @Override
    public void onBindViewHolder(RideHolder holder, int position) {
        Ride currentRide = rides.get(position);
        holder.mDistanceView.setText(String.valueOf(currentRide.getTotalDistance()));
        holder.mStartTimeView.setText(String.valueOf(currentRide.getStartTime()));
        holder.mDurationView.setText(String.valueOf(currentRide.getStartTime()));
        holder.mDateView.setText(currentRide.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }


    public class RideHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView mDateView;
            private TextView mDurationView;
            private TextView mStartTimeView;
            private TextView mDistanceView;

            public RideHolder(View itemView) {
                super(itemView);

                mDateView = itemView.findViewById(R.id.item_date_view);
                mDurationView = itemView.findViewById(R.id.item_duration_view);
                mStartTimeView = itemView.findViewById(R.id.item_starttime_view);
                mDistanceView = itemView.findViewById(R.id.item_distance_view);

            }

            @Override
            public void onClick(View v) {

            }
    }
}
