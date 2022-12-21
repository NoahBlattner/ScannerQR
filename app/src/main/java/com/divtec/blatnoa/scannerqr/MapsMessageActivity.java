package com.divtec.blatnoa.scannerqr;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;

public class MapsMessageActivity extends FragmentActivity implements FragmentResultListener {

    FrameLayout mapsFragment;
    FrameLayout messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_messages);

        // Get the fragments
        mapsFragment = findViewById(R.id.map_frame);
        messageFragment = findViewById(R.id.message_frame);

        // Load the fragments
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_frame, new MapsFragment())
                .replace(R.id.message_frame, new MessageFragment())
                .commit();
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (requestKey.equals("latLng")) {
        }
    }
}