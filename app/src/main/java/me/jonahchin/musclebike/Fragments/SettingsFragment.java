package me.jonahchin.musclebike.Fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import me.jonahchin.musclebike.Interfaces.RideDao;
import me.jonahchin.musclebike.MainActivity;
import me.jonahchin.musclebike.R;

/**
 * Created by jonahchin on 2017-11-14.
 */

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView title = view.findViewById(R.id.title_bar_title);
        title.setText("Settings");

        ConstraintLayout deleteRidesButton = view.findViewById(R.id.delete_rides_button);

        deleteRidesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure you want to delete all your ride data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Deleting all data...", Toast.LENGTH_SHORT).show();
                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        RideDao dao = MainActivity.mAppDatabase.rideDao();
                                        dao.nukeTable();
                                        return "DONE";
                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

        return view;
    }
}
