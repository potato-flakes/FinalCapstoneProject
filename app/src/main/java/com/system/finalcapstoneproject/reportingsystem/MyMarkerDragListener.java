package com.system.finalcapstoneproject.reportingsystem;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;

public class MyMarkerDragListener implements Marker.OnMarkerDragListener {
    private boolean isMarkerDragEnabled = false; // Track the drag state of the marker
    private Geocoder geocoder; // Declare the Geocoder object'
    private TextView locationTextView;
    private double latitude;
    private double longitude;
    private LocationBottomSheetFragment locationBottomSheetFragment;
    private MapView mapView;

    public MyMarkerDragListener(Geocoder geocoder, TextView locationTextView, double latitude, double longitude, LocationBottomSheetFragment fragment, MapView mapView) {
        this.geocoder = geocoder;
        this.locationTextView = locationTextView;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationBottomSheetFragment = fragment;
        this.mapView = mapView;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // Handle marker drag in progress
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        double newLatitude = marker.getPosition().getLatitude();
        double newLongitude = marker.getPosition().getLongitude();

        // Update the latitude and longitude variables
        latitude = newLatitude;
        longitude = newLongitude;

        // Update the location in the LocationBottomSheetFragment
        locationBottomSheetFragment.updateLocation(latitude, longitude);

        // Reverse geocode the latitude and longitude to get the location address
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Set the location only if it's not already set
                locationTextView.setText(address.getAddressLine(0));
                // Update the desired location with the new latitude and longitude
                // You can perform any necessary actions here, such as updating the UI or saving the location
                // Center the map view to the new marker position with animation
                mapView.getController().animateTo(new GeoPoint(latitude, longitude));
                Log.d("MyMarkerDragListener", "onMarkerDragEnd - New value of Latitudes :" + latitude);
                Log.d("MyMarkerDragListener", "onMarkerDragEnd - New value of Longitudes :" + longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // Check if dragging is enabled
        if (!isMarkerDragEnabled) {
            return;
        }
        // Handle marker drag start
    }

    public void setDragEnabled(boolean enabled) {
        isMarkerDragEnabled = enabled;
    }
}
