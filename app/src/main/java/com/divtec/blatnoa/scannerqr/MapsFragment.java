package com.divtec.blatnoa.scannerqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final int ZOOM_LEVEL = 9;

    private GoogleMap mMap;

    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get the fragment and start loading the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Get the coordinates
        Bundle latLng = getActivity().getIntent().getExtras();
        if (latLng != null) {
            latitude = latLng.getDouble("latitude");
            longitude = latLng.getDouble("longitude");
        }

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set th initial marker
        setInitialMarker();

        // On click of the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                // Replace the marker
                setNewMarker(latLng);
            }
        });

    }

    /**
     * Place the initial marker on the map
     */
    private void setInitialMarker() {
        // Add a marker on the start location and move the camera to it
        LatLng point = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(point).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, ZOOM_LEVEL));
        putLngLat();
    }

    /**
     * Set a new marker on the map at the given location
     * @param latLng the location to place the marker at
     */
    private void setNewMarker(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // if zoom level is too low, zoom in
        if (mMap.getCameraPosition().zoom < ZOOM_LEVEL) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
        }

        putLngLat();
    }

    /**
     * Put the latitude and longitude in the fragment manager
     */
    private void putLngLat() {
        // Pass the latitude and longitude to the fragmentmanager
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);

        getActivity().getSupportFragmentManager().setFragmentResult("latLng", bundle);
    }
}
