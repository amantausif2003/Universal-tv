package com.remote.control.allsmarttv.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.remote.control.allsmarttv.Activities.RemoteActivity;
import com.remote.control.allsmarttv.R;

public class ConnFrag extends Fragment {
    private Button disconnect;
    private View progress_bar;
    ImageView exit;
    private RemoteActivity remoteActivity;
    private boolean isPaused = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.remoteActivity = (RemoteActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment can only be added to CoreRemoteActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.connection_fragment, viewGroup, false);
        this.progress_bar = inflate.findViewById(R.id.connect_progress);
        this.disconnect = (Button) inflate.findViewById(R.id.disconnect_btn);
        this.exit =  inflate.findViewById(R.id.cancel_button);

        this.disconnect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                        ConnFrag.this.remoteActivity.cancelDisconnect();


            }
        });


        this.exit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                 remoteActivity.generateRemoteFragment(0);
            }
        });

        return inflate;
    }

    public void uiState(int i) {
        if (!this.isPaused) {

            switch (i) {
                case 1:
                    this.progress_bar.setVisibility(View.GONE);
                    this.disconnect.setVisibility(View.VISIBLE);
                    this.disconnect.setText("Disconnect");
                    return;
                case 2:
                case 3:
                case 5:
                    this.progress_bar.setVisibility(View.VISIBLE);
                    this.disconnect.setVisibility(View.GONE);
                    return;
                case 4:
                default:
                    Log.e("TAG", "Should not show ConnectionFragment if connection has failed");
                    return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isPaused = false;
        uiState(this.remoteActivity.getTheStatus());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.isPaused = true;
    }
}
