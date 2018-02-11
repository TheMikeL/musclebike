package me.jonahchin.musclebike.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.R;
import me.jonahchin.musclebike.Utility.StringUtil;

/**
 * Created by jonahchin on 2017-11-14.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.RideHolder>{

    private List<Ride> rides;
    onItemClickListener mClickListener;


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
        holder.mDistanceView.setText(String.valueOf(currentRide.getDistance()) + "km");
        holder.mDurationView.setText(StringUtil.getHourMinuteSecondFromMillis(currentRide.getElapsedTime()));
        holder.mDateView.setText(StringUtil.getTitleDateFromMillis(currentRide.getRideId()));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public interface onItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setClickListener(final onItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }


    public class RideHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView mDateView;
            private TextView mDurationView;
            private TextView mDistanceView;

            public RideHolder(View itemView) {
                super(itemView);

                mDateView = itemView.findViewById(R.id.item_date_view);
                mDurationView = itemView.findViewById(R.id.item_duration_view);
                mDistanceView = itemView.findViewById(R.id.item_distance_view);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
    }
}
