package com.system.finalcapstoneproject.reportingsystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.system.finalcapstoneproject.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationBottomSheetFragment extends BottomSheetDialogFragment {
    private MapView mapView;
    private TextView locationTextView;
    private Marker userMarker;
    private ImageButton setLocationManuallyButton;
    private boolean isButtonSelected = false;
    private AutoCompleteTextView searchEditText;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationSelectionListener locationSelectionListener;
    private double latitude;
    private double longitude;
    private Geocoder geocoder;
    private String selectedSuggestion;
    // Declare a member variable to hold the userData
    private UserData userData;

    // Constructor that accepts userData
    public LocationBottomSheetFragment(UserData userData) {
        this.userData = userData;
    }

    public static LocationBottomSheetFragment newInstance(UserData userData, double latitude, double longitude) {
        LocationBottomSheetFragment fragment = new LocationBottomSheetFragment(userData);
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }

    public void setLocationSelectionListener(LocationSelectionListener listener) {
        this.locationSelectionListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reporting_fragment_location_bottom_sheet, container, false);
        mapView = view.findViewById(R.id.mapViews);
        locationTextView = view.findViewById(R.id.TutorialDescriptionTextView);
        searchEditText = view.findViewById(R.id.searchEditText);

        setupAutoCompleteSuggestions();

        setLocationManuallyButton = view.findViewById(R.id.setLocationManuallyButton);
        setLocationManuallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isButtonSelected = !isButtonSelected;
                updateSetManualLocationButtonState();
                Log.d("LocationBottomSheet", "setLocationManuallyButton clicked: isButtonSelected = " + isButtonSelected);
            }
        });

        // Set the initial state of the button
        updateSetManualLocationButtonState();

        ImageButton setLocationAutomaticallyButton = view.findViewById(R.id.setLocationAutomaticallyButton);
        setLocationAutomaticallyButton.setBackgroundResource(R.drawable.reporting_toggle_button_unselected_background);
        setLocationAutomaticallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationAutomatically();
            }
        });

        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                performSearch(selectedSuggestion);
                Log.d("LocationBottomSheet", "searchButton clicked");
            }
        });

        searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSuggestion = (String) parent.getItemAtPosition(position);
                searchEditText.setText(selectedSuggestion);
                Log.d("LocationBottomSheet", "AutoCompleteTextView item clicked: " + selectedSuggestion);
                // Call the performSearch method here
                performSearch(selectedSuggestion);
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String searchQuery = searchEditText.getText().toString();
                    performSearch(searchQuery);
                    return true;
                }
                return false;
            }
        });


        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSave();
                Log.d("LocationBottomSheet", "saveButton clicked");
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
            // Reverse geocode the latitude and longitude to get the location address
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    // Set the location only if it's not already set
                    locationTextView.setText(address.getAddressLine(0));
                    Log.d("LocationBottomSheet", "Initial location set to: " + address.getAddressLine(0));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Use the mapView instance to set the map view to the user's location
            mapView.getController().setCenter(new GeoPoint(latitude, longitude));
            userMarker = new Marker(mapView);
            userMarker.setPosition(new GeoPoint(latitude, longitude));
            userMarker.setOnMarkerDragListener(new MyMarkerDragListener(geocoder, locationTextView, latitude, longitude, LocationBottomSheetFragment.this, mapView));
            // Set the marker drag listener
            mapView.getOverlays().add(userMarker);

            // Animate to the user's location with zoom
            final double zoomLevel = 18.5; // Set your desired zoom level as a double
            mapView.getController().setZoom(zoomLevel);
        }

        return view;
    }

    private void setLocationAutomatically() {
        // Handle button click event
        if (userMarker != null) {
            // Clear previous markers from the map
            mapView.getOverlays().clear();

            // Get the current location of the user
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request location permissions if not granted
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                return;
            }
            // Request location updates
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Reverse geocode the current location to get the address
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);

                            // Set the locationTextView with the current address
                            locationTextView.setText(address.getAddressLine(0));
                            Log.d("LocationBottomSheet", "Location automatically set to: " + address.getAddressLine(0));

                            // Set the map view to the current location
                            mapView.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));

                            // Add a marker at the current location
                            userMarker = new Marker(mapView);
                            userMarker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));

                            // Set the new marker's drag listener
                            userMarker.setOnMarkerDragListener(new MyMarkerDragListener(geocoder, locationTextView, latitude, longitude, LocationBottomSheetFragment.this, mapView));

                            mapView.getOverlays().add(userMarker);

                            // Set the initial state of the button
                            isButtonSelected = false;
                            updateSetManualLocationButtonState();

                            // Animate to the current location with zoom
                            final double zoomLevel = 18.5; // Set your desired zoom level as a double
                            mapView.getController().setZoom(zoomLevel);

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    } catch (IOException e) {
                        Log.e("LocationError", "Error retrieving current location: " + e.getMessage());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
        }
    }

    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        // Reverse geocode the latitude and longitude to get the location address
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Set the location only if it's not already set
                locationTextView.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("LocationError", "Error retrieving location: " + e.getMessage());
        }
    }

    private void performSave() {
        // Retrieve the updated latitude and longitude values
        double passLatitude = latitude;
        double passLongitude = longitude;

        if (locationSelectionListener != null) {
            userData.setLocationEnabled(false);
            Log.e("LocationBottomSheetFragment", "isLocationEnabled: " + userData.isLocationEnabled());
            locationSelectionListener.handleLocation(passLatitude, passLongitude);
            Log.e("LocationBottomSheetFragment", "Updated Latitudes: " + passLatitude);
            Log.e("LocationBottomSheetFragment", "Updated Longitudes: " + passLongitude);
            dismiss();
        }
    }


    private void performSearch(String selectedSuggestion) {
        String searchText = searchEditText.getText().toString().trim();
        if (!searchText.isEmpty()) {
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(searchText + ", Pampanga, Philippines", 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();

                    // Clear previous markers from the map
                    mapView.getOverlays().clear();

                    // Update the map view to the searched location
                    mapView.getController().animateTo(new GeoPoint(latitude, longitude));

                    // Add a marker at the searched location
                    userMarker = new Marker(mapView);
                    userMarker.setPosition(new GeoPoint(latitude, longitude));

                    // Set the new marker's drag listener
                    userMarker.setOnMarkerDragListener(new MyMarkerDragListener(geocoder, locationTextView, latitude, longitude, LocationBottomSheetFragment.this, mapView));

                    mapView.getOverlays().add(userMarker);

                    // Update the locationTextView with the searched address
                    locationTextView.setText(address.getAddressLine(0));

                    // Set the initial state of the button
                    isButtonSelected = false;
                    updateSetManualLocationButtonState();

                    // Animate to the searched location with zoom
                    final double zoomLevel = 18.5; // Set your desired zoom level as a double
                    mapView.getController().setZoom(zoomLevel);

                    Log.d("LocationBottomSheet", "Location searched: " + address.getAddressLine(0));
                } else {
                    // Display a message that the searched location is not found
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    Log.d("LocationBottomSheet", "Location not found");
                }
            } catch (IOException e) {
                Log.e("SearchError", "Error performing search: " + e.getMessage());
            }
        }
    }

    private void setupAutoCompleteSuggestions() {
        // Create a list of suggestions
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Balantacan");
        suggestions.add("Bancal Pugad");
        suggestions.add("Bancal Sinubli");
        suggestions.add("Baruya (San Rafael)");
        suggestions.add("Calangain");
        suggestions.add("Concepcion");
        suggestions.add("De La Paz");
        suggestions.add("Del Carmen");
        suggestions.add("Don Ignacio Dimson");
        suggestions.add("Lourdes (Lauc Pau)");
        suggestions.add("Prado Siongco");
        suggestions.add("Remedios");
        suggestions.add("San Agustin");
        suggestions.add("San Antonio");
        suggestions.add("San Francisco");
        suggestions.add("San Isidro");
        suggestions.add("San Jose Apunan");
        suggestions.add("San Jose Gumi");
        suggestions.add("San Juan (Poblacion)");
        suggestions.add("San Matias");
        suggestions.add("San Miguel");
        suggestions.add("San Nicolas 1st (Poblacion)");
        suggestions.add("San Nicolas 2nd");
        suggestions.add("San Pablo 1st");
        suggestions.add("San Pablo 2nd");
        suggestions.add("San Pedro Palcarangan");
        suggestions.add("San Pedro Saug");
        suggestions.add("San Roque Arbol");
        suggestions.add("San Roque Dau");
        suggestions.add("San Vicente");
        suggestions.add("Santa Barbara");
        suggestions.add("Santa Catalina");
        suggestions.add("Santa Cruz");
        suggestions.add("Santa Lucia (Poblacion)");
        suggestions.add("Santa Maria");
        suggestions.add("Santa Monica");
        suggestions.add("Santa Rita");
        suggestions.add("Santa Teresa 1st");
        suggestions.add("Santa Teresa 2nd");
        suggestions.add("Santiago");
        suggestions.add("Santo Cristo");
        suggestions.add("Santo Domingo");
        suggestions.add("Santo Niño (Prado Saba)");
        suggestions.add("Santo Tomas (Poblacion)");

        // Create a custom ArrayAdapter with a custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                R.layout.reporting_item_location_suggestion, // Custom layout resource
                R.id.textViewLocationSuggestion, // TextView resource ID in the custom layout
                suggestions
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.textViewLocationSuggestion);
                ImageView imageView = view.findViewById(R.id.imageViewLocationIcon);
                imageView.setImageResource(R.drawable.ic_barangay_list); // Set the location icon

                return view;
            }
        };

        searchEditText.setAdapter(adapter);
    }

    private void updateSetManualLocationButtonState() {
        if (isButtonSelected) {
            setLocationManuallyButton.setBackgroundResource(R.drawable.reporting_toggle_button_selected_background);
            setLocationManuallyButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_toggle_button_icon_pressed)); // Change the icon color
            dragMarkerEnabled(true); // Enable marker dragging
        } else {
            setLocationManuallyButton.setBackgroundResource(R.drawable.reporting_toggle_button_unselected_background);
            setLocationManuallyButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_toggle_button_icon)); // Reset the icon color
            dragMarkerEnabled(false); // Disable marker dragging
        }
    }

    // Update the state and text of the setLocationButton based on the marker dragging state
    private void dragMarkerEnabled(boolean enabled) {
        if (userMarker != null) {
            userMarker.setDraggable(enabled);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Disable dragging of the bottom sheet
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setDraggable(false);
        });
    }
}
