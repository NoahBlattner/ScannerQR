package com.divtec.blatnoa.scannerqr;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;

public class MapsMessageActivity extends FragmentActivity implements FragmentResultListener {

    MapsFragment mapsFragment;
    MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_messages);

        // Create the fragments
        messageFragment = new MessageFragment();
        mapsFragment = new MapsFragment();

        // Load the fragments
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.message_frame, messageFragment)
                .replace(R.id.map_frame, mapsFragment)
                .commit();

        getSupportFragmentManager().setFragmentResultListener("latLng", this, this);

    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (requestKey.equals("latLng") && messageFragment != null) {

            messageFragment.updateDefaultMessage(
                    result.getDouble("latitude"),
                    result.getDouble("longitude"));
        }
    }
}