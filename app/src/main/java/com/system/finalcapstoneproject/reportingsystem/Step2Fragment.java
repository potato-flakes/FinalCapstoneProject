package com.system.finalcapstoneproject.reportingsystem;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.system.finalcapstoneproject.BuildConfig;
import com.system.finalcapstoneproject.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class Step2Fragment extends Fragment implements LocationSelectionListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String formattedDate;
    private List<String> barangayOptions = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private static final int RESULT_OK = Activity.RESULT_OK;
    private static final int PICK_IMAGES_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextInputLayout textInputLayoutPersonName;
    private TextInputLayout barangayTextInputLayout;
    private TextInputLayout crimeDescriptionTextInputLayout;
    private TextInputLayout evidenceTextInputLayout;
    private LinearLayout imageContainer;
    private LinearLayout typeOfCrimeLayout;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MapView mapView;
    private Button todayButton;
    private Button yesterdayButton;
    private Button selectDateButton;
    private Button viewMaps;
    private Button barangaySpinner;
    private Button yesButton;
    private Button noButton;
    private Button backButton;
    private LinearLayout imageLayout;
    private HorizontalScrollView imageScrollView;
    private FrameLayout addImageContainer;
    private Button nextButton;
    private double latitude;
    private double longitude;
    double setLatitude;
    double setLongitude;
    private TextView textViewValue;
    private TextView locationTextView;
    private TextView dateTextView;
    private TextView timeLabel;
    private TextView amTextView;
    private TextView pmTextView;
    private TextView personNameLabel;
    private TextView gifTextView;
    private TextView currentLocationTextView;
    private ImageView hourIncreaseButton;
    private ImageView hourDecreaseButton;
    private ImageView minuteIncreaseButton;
    private ImageView minuteDecreaseButton;
    private ImageView iconImageView;
    private ImageView locationImageView;
    private ImageView barangayErrorImageView;
    private ImageView enterPersonNameErrorImageView;
    private ImageView crimeDescriptionErrorImageView;
    private ImageView evidenceErrorImageView;
    private EditText hourEditText;
    private EditText minuteEditText;
    private EditText enterPersonEditTexts;
    private boolean isLocationSet;
    private boolean isAffectedByOtherFunction;
    private Marker userMarker;
    private Switch setLocationButton;
    private ProgressBar progressBar;
    private ProgressBar autoGenerateProgressBar;
    private ProgressBar currentLocationProgressBar;
    private GifImageView loadingImageView;
    private UserData userData;
    private LinearLayout animatedLayout;
    private RelativeLayout buttonLayout;
    private Handler handler = new Handler();

    int hourOfDay;
    int prmyClr;
    double phLatitude = 12.8797;
    double phLongitude = 121.7740;
    double zoomLevel;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    private EditText descriptionEditText;
    private Button autoGenerateButton;

    private String generatedDescription;
    private int typingSpeed = 50; // Adjust the typing speed (in milliseconds)
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(generatedDescription)) {
                String currentText = descriptionEditText.getText().toString();
                if (currentText.length() < generatedDescription.length()) {
                    descriptionEditText.setText(generatedDescription.substring(0, currentText.length() + 1));
                    typingHandler.postDelayed(this, typingSpeed);
                }
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reporting_fragment_step2, container, false);

        Log.e("Step1Fragment", "You are in Step2Fragment");
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        textViewValue = view.findViewById(R.id.textViewValue);
        todayButton = view.findViewById(R.id.todayButton);
        yesterdayButton = view.findViewById(R.id.yesterdayButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        imageContainer = view.findViewById(R.id.imageLayout);
        mapView = view.findViewById(R.id.mainMapView);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        viewMaps = view.findViewById(R.id.viewMapsButton);
        locationTextView = view.findViewById(R.id.TutorialDescriptionTextView);
        dateTextView = view.findViewById(R.id.TutorialCategoryTextView);
        iconImageView = view.findViewById(R.id.iconImageView);
        timeLabel = view.findViewById(R.id.timeLabel);
        hourEditText = view.findViewById(R.id.hourEditText);
        minuteEditText = view.findViewById(R.id.minuteEditText);
        amTextView = view.findViewById(R.id.amTextView);
        pmTextView = view.findViewById(R.id.pmTextView);
        hourIncreaseButton = view.findViewById(R.id.hourIncreaseButton);
        hourDecreaseButton = view.findViewById(R.id.hourDecreaseButton);
        minuteIncreaseButton = view.findViewById(R.id.minuteIncreaseButton);
        minuteDecreaseButton = view.findViewById(R.id.minuteDecreaseButton);
        barangaySpinner = view.findViewById(R.id.barangaySpinner);
        progressBar = view.findViewById(R.id.progressBar);
        locationImageView = view.findViewById(R.id.locationImageView);
        loadingImageView = view.findViewById(R.id.loadingImageView);
        gifTextView = view.findViewById(R.id.gifTextView);
        barangayErrorImageView = view.findViewById(R.id.barangayErrorImageView);
        personNameLabel = view.findViewById(R.id.personNameLabel);
        enterPersonEditTexts = view.findViewById(R.id.enterPersonEditTexts);
        animatedLayout = view.findViewById(R.id.animatedLayout);
        buttonLayout = view.findViewById(R.id.buttonLayout);
        prmyClr = ContextCompat.getColor(requireContext(), R.color.colorPrimary);
        backButton = view.findViewById(R.id.backButton);
        imageLayout = view.findViewById(R.id.imageLayout);
        imageScrollView = view.findViewById(R.id.imageScrollView);
        addImageContainer = view.findViewById(R.id.addImageContainer);
        nextButton = view.findViewById(R.id.nextButton);
        autoGenerateButton = view.findViewById(R.id.autoGenerateButton);
        enterPersonNameErrorImageView = view.findViewById(R.id.enterPersonNameErrorImageView);
        crimeDescriptionErrorImageView = view.findViewById(R.id.crimeDescriptionErrorImageView);
        evidenceErrorImageView = view.findViewById(R.id.evidenceErrorImageView);
        textInputLayoutPersonName = view.findViewById(R.id.textInputLayoutPersonName);
        barangayTextInputLayout = view.findViewById(R.id.barangayTextInputLayout);
        crimeDescriptionTextInputLayout = view.findViewById(R.id.crimeDescriptionTextInputLayout);
        evidenceTextInputLayout = view.findViewById(R.id.evidenceTextInputLayout);
        typeOfCrimeLayout = view.findViewById(R.id.typeOfCrimeLayout);
        autoGenerateProgressBar = view.findViewById(R.id.autoGenerateProgressBar);
        currentLocationTextView = view.findViewById(R.id.currentLocationTextView);
        currentLocationProgressBar = view.findViewById(R.id.currentLocationProgressBar);

        typeOfCrimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToStep1Fragment();
            }
        });

        // Find the Yes and No buttons
        yesButton = view.findViewById(R.id.yesButton);
        noButton = view.findViewById(R.id.noButton);

        // Change the background color of the Yes button to colorPrimary
        yesButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
        // Revert the background color of the No button to the default color
        noButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

        // Change the text color of the No button to colorPrimary
        noButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        // Revert the text color of the Yes button to the default color
        yesButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Set click listeners for the buttons
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the background color of the Yes button to colorPrimary
                yesButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
                // Revert the background color of the No button to the default color
                noButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

                // Change the text color of the No button to colorPrimary
                noButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                // Revert the text color of the Yes button to the default color
                yesButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                // Apply the click animation
                applyClickAnimation(R.animator.reporting_button_scale, v);

                // Slide up the views using TransitionManager
                TransitionManager.beginDelayedTransition((ViewGroup) animatedLayout.getParent());
                animatedLayout.setVisibility(View.VISIBLE);

                userData.setYesButtonSelected(true);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the background color of the No button to colorPrimary
                noButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
                // Revert the background color of the Yes button to the default color
                yesButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

                // Change the text color of the Yes button to colorPrimary
                yesButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                // Revert the text color of the No button to the default color
                noButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                // Apply the click animation
                applyClickAnimation(R.animator.reporting_button_scale, v);

                // Slide down the views using TransitionManager
                TransitionManager.beginDelayedTransition((ViewGroup) animatedLayout.getParent());
                animatedLayout.setVisibility(View.GONE);

                userData.setYesButtonSelected(false);
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the selected state for the buttons
                todayButton.setSelected(true);
                yesterdayButton.setSelected(false);
                selectDateButton.setSelected(false);

                // Set the text colors and backgrounds for the buttons
                todayButton.setTextColor(getResources().getColor(R.color.selected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.unselected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));

                // Get the current date
                Calendar currentDate = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                formattedDate = dateFormat.format(currentDate.getTime());

                userData.setCrimeDate(formattedDate);

                Log.d("IDUNNO", "Today Button was clicked");
                Log.d("IDUNNO", "Today's Date is: " + formattedDate);
            }
        });

        yesterdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the selected state for the buttons
                todayButton.setSelected(false);
                yesterdayButton.setSelected(true);
                selectDateButton.setSelected(false);

                // Set the text colors and backgrounds for the buttons
                todayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.selected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.unselected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));

                // Get the previous day's date
                Calendar previousDate = Calendar.getInstance();
                previousDate.add(Calendar.DAY_OF_MONTH, -1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                formattedDate = dateFormat.format(previousDate.getTime());

                userData.setCrimeDate(formattedDate);

                Log.d("IDUNNO", "Yesterday Button was clicked");
                Log.d("IDUNNO", "Yesterday's Date is: " + formattedDate);
            }
        });

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the selected state for the buttons
                todayButton.setSelected(false);
                yesterdayButton.setSelected(false);
                selectDateButton.setSelected(true);

                // Set the text colors and backgrounds for the buttons
                todayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.selected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));

                showDatePicker();
            }
        });

        // Find the ImageView by its ID
        ImageView iconImageView = view.findViewById(R.id.iconImageView);
        // Set the click listener for the ImageView
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                onDateIconClick();
            }
        });

        // Find your hour and minute EditText fields by their IDs
        EditText hourEditText = view.findViewById(R.id.hourEditText);
        EditText minuteEditText = view.findViewById(R.id.minuteEditText);

        // Set the maximum length for the hour and minute EditText fields
        int maxDigits = 2; // Limit to 2 digits
        hourEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxDigits)});
        minuteEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxDigits)});

        // Set an InputFilter to limit the hour EditText to the range from 1 to 12
        hourEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax("1", "12")
        });

        // Set an InputFilter to limit the minute EditText to the range from 0 to 59
        minuteEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax("0", "59")
        });

        hourIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "Hour increase Button was clicked");
                int hour = Integer.parseInt(hourEditText.getText().toString());
                if (hour < 12) {
                    hour++;
                } else {
                    hour = 1;
                }
                hourEditText.setText(String.valueOf(hour));
            }
        });

        hourDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "Hour decrease Button was clicked");
                int hour = Integer.parseInt(hourEditText.getText().toString());
                if (hour > 1) {
                    hour--;
                } else {
                    hour = 12;
                }
                hourEditText.setText(String.valueOf(hour));
            }
        });

        minuteIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "Minute increase Button was clicked");
                int minute = Integer.parseInt(minuteEditText.getText().toString());
                if (minute < 59) {
                    minute++;
                } else {
                    minute = 0;
                }
                minuteEditText.setText(String.format("%02d", minute));
            }
        });

        minuteDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "Hour decrease Button was clicked");
                int minute = Integer.parseInt(minuteEditText.getText().toString());
                if (minute > 0) {
                    minute--;
                } else {
                    minute = 59;
                }
                minuteEditText.setText(String.format("%02d", minute));
            }
        });

        // Set the click listeners for AM and PM TextViews
        amTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "AM Button was clicked");
                amTextView.setTextColor(prmyClr);
                pmTextView.setTextColor(Color.GRAY);
            }
        });

        pmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDUNNO", "PM Button was clicked");
                amTextView.setTextColor(Color.GRAY);
                pmTextView.setTextColor(prmyClr);
            }
        });

        imageUrls = new ArrayList<>();
        loadUserData();
        loadUserLocation();

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        barangayOptions.add("Balantacan");
        barangayOptions.add("Bancal Pugad");
        barangayOptions.add("Bancal Sinubli");
        barangayOptions.add("Baruya (San Rafael)");
        barangayOptions.add("Calangain");
        barangayOptions.add("Concepcion");
        barangayOptions.add("De La Paz");
        barangayOptions.add("Del Carmen");
        barangayOptions.add("Don Ignacio Dimson");
        barangayOptions.add("Lourdes (Lauc Pau)");
        barangayOptions.add("Prado Siongco");
        barangayOptions.add("Remedios");
        barangayOptions.add("San Agustin");
        barangayOptions.add("San Antonio");
        barangayOptions.add("San Francisco");
        barangayOptions.add("San Isidro");
        barangayOptions.add("San Jose Apunan");
        barangayOptions.add("San Jose Gumi");
        barangayOptions.add("San Juan (Poblacion)");
        barangayOptions.add("San Matias");
        barangayOptions.add("San Miguel");
        barangayOptions.add("San Nicolas 1st (Poblacion)");
        barangayOptions.add("San Nicolas 2nd");
        barangayOptions.add("San Pablo 1st");
        barangayOptions.add("San Pablo 2nd");
        barangayOptions.add("San Pedro Palcarangan");
        barangayOptions.add("San Pedro Saug");
        barangayOptions.add("San Roque Arbol");
        barangayOptions.add("San Roque Dau");
        barangayOptions.add("San Vicente");
        barangayOptions.add("Santa Barbara");
        barangayOptions.add("Santa Catalina");
        barangayOptions.add("Santa Cruz");
        barangayOptions.add("Santa Lucia (Poblacion)");
        barangayOptions.add("Santa Maria");
        barangayOptions.add("Santa Monica");
        barangayOptions.add("Santa Rita");
        barangayOptions.add("Santa Teresa 1st");
        barangayOptions.add("Santa Teresa 2nd");
        barangayOptions.add("Santiago");
        barangayOptions.add("Santo Cristo");
        barangayOptions.add("Santo Domingo");
        barangayOptions.add("Santo Niño (Prado Saba)");
        barangayOptions.add("Santo Tomas (Poblacion)");

        barangaySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        setLocationButton = view.findViewById(R.id.locationSwitchs);
        // Restore the switch status from saved data (if available)
        boolean switchStatus = userData.isLocationEnabled();
        setLocationButton.setChecked(switchStatus);
        setLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.setLocationEnabled(isChecked);
                    setLocation();
                    Log.d("IDUNNO", "setLocationButton - Switch Button was turned ON");
                } else {
                    userData.setLocationEnabled(isChecked);
                    // If the switch is turned off, reset the location flag
                    isLocationSet = false;
                    if (!isAffectedByOtherFunction) {
                        removeUserMarker();
                    }
                    stopLocationUpdates();
                    Log.d("IDUNNO", "setLocationButton - Switch Button was turned OFF");
                }
            }
        });

        viewMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationBottomSheet();
                Log.d("IDUNNO", "View Maps Button was clicked");
            }
        });

        if (checkLocationPermission()) {
            // Initialize location manager and listener
            initLocationManager();
        }

        currentLocationProgressBar.setVisibility(View.VISIBLE);

        // Set a click listener for the "Add Image" container
        addImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToStep1Fragment();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            // Inside the onClick() method of the "Next" button in Step2Fragment
            @Override
            public void onClick(View v) {
                if (validateUserInputs()) {
                    saveUserData();
                    // Navigate to the next fragment (EditReportStep3Fragment)
                    ((createReport_activity) requireActivity()).navigateToNextFragment(new Step3Fragment());
                }
            }

        });
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        autoGenerateButton = view.findViewById(R.id.autoGenerateButton);

        autoGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generateValidateUserInputs()) {
                    generatedDescription = generateDescription();
                    descriptionEditText.setText("");
                    autoGenerateButton.setText("");
                    startTypingEffect();

                    autoGenerateButton.setEnabled(false);
                    autoGenerateProgressBar.setVisibility(View.VISIBLE);
                    autoGenerateProgressBar.setIndeterminate(false); // Disable indeterminate mode
                    autoGenerateProgressBar.setMax(generatedDescription.length()); // Set the max progress
                    autoGenerateProgressBar.setProgress(0); // Initialize progress
                    autoGenerateProgressBar.setIndeterminate(true); // Set the ProgressBar to indeterminate mode
                    autoGenerateProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN); // Set the color of the ProgressBar

                    typingHandler.removeCallbacks(typingRunnable); // Cancel previous typing

                    typingHandler.post(new Runnable() {
                        int progress = 0;
                        int length = generatedDescription.length();

                        @Override
                        public void run() {
                            if (progress < length) {
                                descriptionEditText.setText(generatedDescription.substring(0, progress + 1));
                                progress++;
                                autoGenerateProgressBar.setProgress(progress);
                                typingHandler.postDelayed(this, typingSpeed);
                                descriptionEditText.setEnabled(false);
                            } else {
                                autoGenerateProgressBar.setVisibility(View.GONE); // Hide the ProgressBar
                                autoGenerateButton.setText("Regenerate");
                                autoGenerateButton.setEnabled(true); // Re-enable the button
                                descriptionEditText.setEnabled(true);
                            }
                        }
                    });

                } else {
                    // Show an error message to the user indicating missing inputs
                    Toast.makeText(requireContext(), "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        descriptionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void initLocationManager() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location updates
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Convert coordinates to a user-friendly address
                String address = getAddressFromLocation(latitude, longitude);
                currentLocationTextView.setText(address);
                userData.setUserCurrentLocation(currentLocationTextView.getText().toString());
                if(currentLocationTextView != null){
                    currentLocationProgressBar.setVisibility(View.GONE);
                    currentLocationTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle location provider status changes
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle location provider enabled
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle location provider disabled
            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());
        String addressText = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Extract barangay if available
                String barangay = address.getSubLocality();

                // Build the address string
                StringBuilder addressBuilder = new StringBuilder();

                // Include barangay in the address if available
                if (barangay != null && !barangay.isEmpty()) {
                    addressBuilder.append(barangay).append(", ");
                }

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressBuilder.append(", ");
                    }
                }
                addressText = addressBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }
    private boolean generateValidateUserInputs() {
        // Validate user inputs here
        boolean isValid = true;

        if (userData.isYesButtonSelected()) {
            // Validate person involved input
            String personInvolved = enterPersonEditTexts.getText().toString().trim();
            if (personInvolved.isEmpty()) {
                isValid = false;
                setupTextWatchers();
                enterPersonNameErrorImageView.setVisibility(View.VISIBLE);
                textInputLayoutPersonName.setError("Error: Please enter the person name"); // Clear the error
                enterPersonEditTexts.requestFocus();
            } else {
                enterPersonNameErrorImageView.setVisibility(View.GONE);
                textInputLayoutPersonName.setError(null); // Clear the error
            }
        }

        // Validate location input
        String location = barangaySpinner.getText().toString().trim();
        if (location.isEmpty()) {
            isValid = false;
            setupTextWatchers();
            barangayErrorImageView.setVisibility(View.VISIBLE);
            barangayTextInputLayout.setError("Error: Please provide location information");
            barangayTextInputLayout.requestFocus();
        } else {
            barangayErrorImageView.setVisibility(View.GONE);
            barangayTextInputLayout.setError(null); // Clear the error
        }

        return isValid;
    }

    private boolean validateUserInputs() {
        // Validate user inputs here
        boolean isValid = true;

        if (userData.isYesButtonSelected()) {
            // Validate person involved input
            String personInvolved = enterPersonEditTexts.getText().toString().trim();
            if (personInvolved.isEmpty()) {
                isValid = false;
                setupTextWatchers();
                enterPersonNameErrorImageView.setVisibility(View.VISIBLE);
                textInputLayoutPersonName.setError("Error: Please enter the person name"); // Clear the error
                enterPersonEditTexts.requestFocus();
            } else {
                enterPersonNameErrorImageView.setVisibility(View.GONE);
                textInputLayoutPersonName.setError(null); // Clear the error
            }
        }

        // Validate location input
        String location = barangaySpinner.getText().toString().trim();
        if (location.isEmpty()) {
            isValid = false;
            setupTextWatchers();
            barangayErrorImageView.setVisibility(View.VISIBLE);
            barangayTextInputLayout.setError("Error: Please provide location information");
            barangayTextInputLayout.requestFocus();
        } else {
            barangayErrorImageView.setVisibility(View.GONE);
            barangayTextInputLayout.setError(null); // Clear the error
        }

        // Validate location input
        String crime_description = descriptionEditText.getText().toString().trim();
        if (crime_description.isEmpty()) {
            isValid = false;
            setupTextWatchers();
            crimeDescriptionErrorImageView.setVisibility(View.VISIBLE);
            crimeDescriptionTextInputLayout.setError("Error: Please provide a brief description");
            crimeDescriptionTextInputLayout.requestFocus();
        } else {
            crimeDescriptionErrorImageView.setVisibility(View.GONE);
            crimeDescriptionTextInputLayout.setError(null); // Clear the error
        }

        if (userData.getSelectedImageUrls().isEmpty()) {
            isValid = false;
            evidenceErrorImageView.setVisibility(View.VISIBLE);
            evidenceTextInputLayout.setError("Error: Please provide evidences");
            evidenceTextInputLayout.requestFocus();
        } else {
            evidenceErrorImageView.setVisibility(View.GONE);
            evidenceTextInputLayout.setVisibility(View.GONE);
            evidenceTextInputLayout.setError(null); // Clear the error
        }

        return isValid;
    }

    private void setupTextWatchers() {
        enterPersonEditTexts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String personInvolved = charSequence.toString().trim();
                if (personInvolved.isEmpty()) {
                    enterPersonNameErrorImageView.setVisibility(View.VISIBLE);
                    textInputLayoutPersonName.setError("Error: Please enter the person name"); // Clear the error
                    enterPersonEditTexts.requestFocus();
                } else {
                    enterPersonNameErrorImageView.setVisibility(View.GONE);
                    textInputLayoutPersonName.setError(null); // Clear the error
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String crimeDescription = charSequence.toString().trim();
                if (crimeDescription.isEmpty()) {
                    crimeDescriptionErrorImageView.setVisibility(View.VISIBLE);
                    crimeDescriptionTextInputLayout.setError("Error: Please enter crime description"); // Clear the error
                    crimeDescriptionTextInputLayout.requestFocus();
                } else {
                    crimeDescriptionErrorImageView.setVisibility(View.GONE);
                    crimeDescriptionTextInputLayout.setError(null); // Clear the error
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        barangaySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String location = charSequence.toString().trim();
                if (location.isEmpty()) {
                    barangayErrorImageView.setVisibility(View.VISIBLE);
                    barangayTextInputLayout.setError("Please provide location information");
                    barangaySpinner.requestFocus();
                } else {
                    barangayErrorImageView.setVisibility(View.GONE);
                    barangayTextInputLayout.setError(null); // Clear the error
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void startTypingEffect() {
        typingHandler.postDelayed(typingRunnable, typingSpeed);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        typingHandler.removeCallbacks(typingRunnable);
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private String generateDescription() {
        String crimeType = textViewValue.getText().toString();
        String personInvolved = enterPersonEditTexts.getText().toString();
        String date = userData.getCrimeDate();
        String time = getSelectedTime();
        String location = barangaySpinner.getText().toString();
        String[] actions = {"reporting", "documenting", "noticing"};
        String action = actions[new Random().nextInt(actions.length)];

        // Generate the description with dynamic content, crime type, date, time, and location
        String description = "    I am %s an incident of %s that occurred on %s, at around %s near %s.";

        // Incorporate variability and improve grammar and punctuation
        description = String.format(description, action, crimeType, date, time, location);

        if (userData.isYesButtonSelected()) {
            // Conditionally include the person involved if provided
            if (!personInvolved.isEmpty()) {
                description += " The person involved is known and described as: %s.";
                description = String.format(description, personInvolved);
            }
        } else {
            description += " The person involved is unknown.";

        }

        // Add witness account based on crime type
        String[] witnessAccounts = null;
        if (crimeType.equals("Illegal Dumping")) {
            witnessAccounts = new String[]{
                    "I witnessed them illegally disposing of waste in an unauthorized location.",
                    "I observed the person engaged in illegal dumping of trash in a public area.",
                    "I saw them disposing of waste improperly near the scene."
            };
        } else if (crimeType.equals("Vandalism")) {
            witnessAccounts = new String[]{
                    "I witnessed them defacing public property with graffiti.",
                    "I observed them damaging public infrastructure through acts of vandalism.",
                    "I saw them engaging in destructive behavior, causing property damage."
            };
        } else if (crimeType.equals("Noise Pollution")) {
            witnessAccounts = new String[]{
                    "I witnessed excessive noise causing disturbance to the peace and well-being of the community.",
                    "I observed loud noises that disrupted the tranquility of the area and affected residents.",
                    "I heard disturbing levels of noise that were harmful to the environment and public health."
            };
        } else if (crimeType.equals("Air Pollution")) {
            witnessAccounts = new String[]{
                    "I witnessed activities releasing harmful pollutants into the air, such as emissions from industrial processes or vehicles.",
                    "I observed the release of noxious fumes into the atmosphere, contributing to air pollution.",
                    "I saw harmful gases being emitted, posing a threat to air quality and public health."
            };
        } else if (crimeType.equals("Water Pollution")) {
            witnessAccounts = new String[]{
                    "I witnessed contamination or pollution of water bodies, such as rivers, lakes, or oceans.",
                    "I observed the release of pollutants into water sources, endangering aquatic ecosystems.",
                    "I saw waste materials being improperly disposed of, leading to water pollution and ecosystem harm."
            };
        } else if (crimeType.equals("Wildlife Harm")) {
            witnessAccounts = new String[]{
                    "I witnessed incidents that harmed or endangered wildlife, such as illegal hunting or destruction of natural habitats.",
                    "I observed actions that posed a threat to local wildlife and disrupted the natural balance of the ecosystem.",
                    "I saw activities that negatively impacted animal habitats and posed risks to the survival of species."
            };
        } else if (crimeType.equals("Unlawful Construction")) {
            witnessAccounts = new String[]{
                    "I witnessed unauthorized or illegal construction activities that violated building codes or zoning regulations.",
                    "I observed construction taking place without proper permits, violating established regulations.",
                    "I saw illegal construction work being carried out, disregarding the guidelines set for the area."
            };
        } else if (crimeType.equals("Traffic Violations")) {
            witnessAccounts = new String[]{
                    "I witnessed unsafe driving behaviors and traffic violations that posed risks to pedestrians and other road users.",
                    "I observed reckless driving that endangered lives and violated traffic rules.",
                    "I saw instances of dangerous driving that compromised road safety and public well-being."
            };
        } else if (crimeType.equals("Illegal Fishing")) {
            witnessAccounts = new String[]{
                    "I witnessed instances of illegal fishing practices that threatened aquatic ecosystems and sustainability.",
                    "I observed unauthorized fishing activities that had a detrimental impact on marine life and conservation efforts.",
                    "I saw violations of fishing regulations that harmed the balance of aquatic environments and fish populations."
            };
        } else if (crimeType.equals("Hazardous Materials")) {
            witnessAccounts = new String[]{
                    "I witnessed mishandling or improper disposal of hazardous materials that posed risks to public health and the environment.",
                    "I observed hazardous substances being handled without proper precautions, endangering people and surroundings.",
                    "I saw improper management of dangerous chemicals, leading to potential health hazards and environmental harm."
            };
        }


        // Add more witness accounts for other crime types here

        if (witnessAccounts != null) {
            String witnessAccount = witnessAccounts[new Random().nextInt(witnessAccounts.length)];
            description += " " + witnessAccount;
        }

        // Add impact of the incident
        description += "\n\n    This incident has contributed to the degradation of our environment.";

        // Add legal/regulatory context based on crime type
        String[] regulations = null;
        if (crimeType.equals("Illegal Dumping")) {
            regulations = new String[]{
                    "This is a clear violation of local environmental regulations.",
                    "The person who committed this act has violated waste disposal ordinances.",
                    "Such actions are against the municipal code on proper waste management."
            };
        } else if (crimeType.equals("Vandalism")) {
            regulations = new String[]{
                    "This incident is a clear violation of laws against property damage and vandalism.",
                    "The person responsible for this act has violated regulations prohibiting defacement.",
                    "Engaging in acts of vandalism is against the legal framework for community preservation."
            };
        } else if (crimeType.equals("Noise Pollution")) {
            regulations = new String[]{
                    "This incident is in violation of noise regulations that aim to maintain community well-being.",
                    "The excessive noise generated is against local laws that ensure peace and quiet for residents.",
                    "Such noise pollution constitutes a breach of established regulations governing noise levels."
            };
        } else if (crimeType.equals("Air Pollution")) {
            regulations = new String[]{
                    "This incident is a violation of environmental laws that regulate air quality and pollutant emissions.",
                    "The release of harmful pollutants into the air is against established regulations for pollution control.",
                    "Engaging in activities that contribute to air pollution is in defiance of legal frameworks for clean air."
            };
        } else if (crimeType.equals("Water Pollution")) {
            regulations = new String[]{
                    "This incident is in violation of laws protecting water bodies from contamination and pollution.",
                    "The contamination of water sources is against established regulations for water quality preservation.",
                    "Such actions infringe upon legal measures aimed at safeguarding aquatic ecosystems and public health."
            };
        } else if (crimeType.equals("Wildlife Harm")) {
            regulations = new String[]{
                    "This incident is against regulations that protect wildlife and their habitats from harm and destruction.",
                    "The harm inflicted upon wildlife is in violation of established conservation and protection laws.",
                    "Engaging in activities that endanger local fauna goes against legal frameworks for biodiversity conservation."
            };
        } else if (crimeType.equals("Unlawful Construction")) {
            regulations = new String[]{
                    "This incident is a violation of building codes and zoning regulations that govern construction activities.",
                    "The unauthorized construction work is against established laws that ensure proper land use and development.",
                    "Engaging in illegal construction is in defiance of legal guidelines set for urban planning and development."
            };
        } else if (crimeType.equals("Traffic Violations")) {
            regulations = new String[]{
                    "This incident is in violation of traffic laws that promote road safety and responsible driving behaviors.",
                    "The observed traffic violations breach regulations that ensure the safety of pedestrians and motorists.",
                    "Engaging in unsafe driving practices contradicts legal measures put in place to prevent accidents and risks."
            };
        } else if (crimeType.equals("Illegal Fishing")) {
            regulations = new String[]{
                    "This incident is a violation of regulations that govern fishing practices and aquatic resource conservation.",
                    "The illegal fishing practices conducted pose a threat to marine ecosystems and sustainable fisheries.",
                    "Engaging in unauthorized fishing activities is against legal frameworks aimed at maintaining aquatic biodiversity."
            };
        } else if (crimeType.equals("Hazardous Materials")) {
            regulations = new String[]{
                    "This incident is in violation of regulations that oversee the proper handling and disposal of hazardous materials.",
                    "The mishandling of hazardous substances is against established laws that safeguard public health and the environment.",
                    "Engaging in improper management of dangerous chemicals is a breach of legal measures for hazardous waste control."
            };
        }

        // Add more regulations for other crime types here

        if (regulations != null) {
            String regulation = regulations[new Random().nextInt(regulations.length)];
            description += " " + regulation;
        }

        // Add user messages for addressing the report and a "Thank you" message
        String[] userMessages = {
                "Your attention to this matter is appreciated. Please address this report promptly.",
                "Kindly take necessary actions to address this incident. Thank you for your cooperation.",
                "We request your immediate action to resolve this issue. Your prompt response is valued.",
                "Your intervention is required to address this reported incident. Thank you for your understanding."
        };
        String userMessage = userMessages[new Random().nextInt(userMessages.length)];
        description += "\n\n    " + userMessage;

        return description;
    }


    private void saveUserData() {
        // Save the user's input to the userData object
        userData.setCrimePerson(enterPersonEditTexts.getText().toString());
        userData.setSelectedBarangay(barangaySpinner.getText().toString());
        userData.setCrimeDescription(descriptionEditText.getText().toString());
        getSelectedTime();
    }

    private void saveCoordinates(double latitude, double longitude) {
        userData.setCrimeLatitude(latitude);
        userData.setCrimeLongitude(longitude);
    }

    private void loadUserData() {
        // Populate the UI elements with data from the userData object (if available)
        if (userData != null) {
            String crimeType = userData.getCrimeType();
            if (crimeType != null) {
                textViewValue.setText(userData.getCrimeType());
            }

            String person = userData.getCrimePerson();
            if (person != null) {
                enterPersonEditTexts.setText(userData.getCrimePerson());
            }

            String crimeDate = userData.getCrimeDate();
            if (crimeDate != null) {
                // Update the date buttons based on the stored date
                updateDateButtons(crimeDate);
            } else {
                // Set the selected state for the buttons
                todayButton.setSelected(true);
                yesterdayButton.setSelected(false);
                selectDateButton.setSelected(false);

                // Set the text colors and backgrounds for the buttons
                todayButton.setTextColor(getResources().getColor(R.color.selected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.unselected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));

                // Get the current date
                Calendar currentDate = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                formattedDate = dateFormat.format(currentDate.getTime());

                userData.setCrimeDate(formattedDate);

            }

            int crimeHour = userData.getCrimeHour();
            Log.d("IDUNNO", "Current Hour is: " + crimeHour);
            if (crimeHour >= 0) {
                // Time is set, you can use the `crimeHour` value here
                int storedCrimeHour = crimeHour; // Get the hour part
                hourEditText.setText(String.valueOf(storedCrimeHour));
            } else {
                // Get the current time
                Calendar currentTime = Calendar.getInstance();
                hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY);

                // Convert 24-hour format to 12-hour format
                int hour12Format = hourOfDay % 12;
                if (hour12Format == 0) {
                    hour12Format = 12; // 0 should be displayed as 12 in 12-hour format
                }

                // Set the current time to the EditText fields
                hourEditText.setText(String.valueOf(hour12Format));

                Log.d("IDUNNO", "Today Button was clicked");
                Log.d("IDUNNO", "Current Hour is: " + hourEditText.getText().toString());

                // Set the AM/PM states based on the current hour
                if (hourOfDay >= 12) {
                    // PM selected
                    amTextView.setTextColor(Color.GRAY);
                    pmTextView.setTextColor(prmyClr);
                } else {
                    // AM selected
                    amTextView.setTextColor(prmyClr);
                    pmTextView.setTextColor(Color.GRAY);
                }
            }

            int crimeMinute = userData.getCrimeMinute();
            if (crimeMinute >= 0) {
                // Minute is set, you can use the `crimeMinute` value here
                int storedCrimeMinute = crimeMinute; // Get the minute part
                minuteEditText.setText(String.format("%02d", storedCrimeMinute));
            } else {
                // Get the current time
                Calendar currentTime = Calendar.getInstance();
                int minuteOfDay = currentTime.get(Calendar.MINUTE);

                // Set the current minute to the EditText field
                minuteEditText.setText(String.format("%02d", minuteOfDay));
            }

            String storedCrimeTimeIndication = userData.getCrimeTimeIndication();
            if (storedCrimeTimeIndication != null) {
                if (storedCrimeTimeIndication == "PM") {
                    // PM selected
                    amTextView.setTextColor(Color.GRAY);
                    pmTextView.setTextColor(prmyClr);
                } else {
                    // AM selected
                    amTextView.setTextColor(prmyClr);
                    pmTextView.setTextColor(Color.GRAY);
                }
            }

            String storedCrimeBarangay = userData.getSelectedBarangay();
            if (storedCrimeBarangay != null) {
                barangaySpinner.setText(userData.getSelectedBarangay());
            }

            Double storedLatitude = userData.getCrimeLatitude();
            Double storedLongitude = userData.getCrimeLongitude();
            if (storedLatitude != 0.0 && storedLongitude != 0.0) {
                Log.e("Step1Fragment", "onCreateView - if method was launched!");
                setLatitude = storedLatitude;
                setLongitude = storedLongitude;
                latitude = storedLatitude;
                longitude = storedLongitude;
                zoomLevel = 18.0;

                Log.e("Step1Fragment", "onCreateView - storedLatitude value: " + storedLatitude);
                Log.e("Step1Fragment", "onCreateView - storedLongitude value: " + storedLongitude);
            } else {
                // Set default latitude and longitude (e.g., Philippines coordinates)
                Log.e("Step1Fragment", "onCreateView - else method was launched, you failed");
                setLatitude = phLatitude;
                setLongitude = phLongitude;
                zoomLevel = 2.5;
            }

            String storedCrimeExactLocation = userData.getCrimeExactLocation();
            if (storedCrimeExactLocation != null) {
                locationTextView.setText(userData.getCrimeExactLocation());
            }

            String storeCrimeDescription = userData.getCrimeDescription();
            if (storeCrimeDescription != null) {
                descriptionEditText.setText(userData.getCrimeDescription());
            }

            String storeImages = userData.getSelectedImageUrls().toString();
            if (storeImages != null) {
                displayImages();
            }

        } else {
            Log.e("Step1Fragment", "onCreateView - userData is null");
        }
    }

    //START OF LOCATION METHODS
    private void setLocation() {
        Log.d("IDUNNO", "setLocation - Set Location Button was clicked");
        // Show the loading GIF
        loadingImageView.setVisibility(View.VISIBLE);

        removeUserMarker();

        if (!setLocationButton.isChecked()) {
            Log.e("IDUNNO", "Switch button was turned off");
            // If the switch is turned off, reset the location flag and return
            isLocationSet = false;
            // Hide the loading GIF
            loadingImageView.setVisibility(View.GONE);

            return;
        }

        if (!isLocationSet) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (setLocationButton.isChecked() && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request location permissions if not granted
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                Log.d("IDUNNO", "setLocationMethod - Permission to open location not allowed");
            }
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    handleLocation(latitude, longitude);
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

    public void handleLocation(double passLatitude, double passLongitude) {
        Log.e("Step2Fragment", "handleLocation - is location enabled in step2" + userData.isLocationEnabled());
        removeUserMarker();
        // Reverse geocode the latitude and longitude to get the location address
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(passLatitude, passLongitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locationTextView.setText(address.getAddressLine(0));
                userData.setCrimeExactLocation(locationTextView.getText().toString());

                new ConvertCoordinatesTask().execute(passLatitude, passLongitude);

                latitude = passLatitude;
                longitude = passLongitude;

                // Update the map view with a marker at the user's location
                GeoPoint userLocation = new GeoPoint(passLatitude, passLongitude);
                userMarker = new Marker(mapView);
                userMarker.setPosition(userLocation);
                mapView.getOverlays().add(userMarker);

                // Animate to the user's location with zoom
                final double zoomLevel = 18.5; // Set your desired zoom level as a double
                mapView.getController().animateTo(userLocation, zoomLevel, null);
                Log.d("IDUNNO", "User location using Geopoint: " + userLocation);
                // Inside the onLocationChanged() method, after updating the UI, hide the progress bar
                // Hide the loading GIF
                loadingImageView.setVisibility(View.GONE);
                gifTextView.setVisibility(View.GONE);
                // Update the flag to indicate that the location is set
                isLocationSet = true;

                if (!userData.isLocationEnabled()) {
                    setLocationButton.setChecked(false);
                    isAffectedByOtherFunction = true;
                    Log.e("Step2Fragment", "isLocationEnabled in Step2Fragment: " + userData.isLocationEnabled());
                }
                stopLocationUpdates();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConvertCoordinatesTask extends AsyncTask<Double, Void, String> {
        private final double LATITUDE_ADJUSTMENT = 0.0001;
        private final double LONGITUDE_ADJUSTMENT = 0.0001;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); // Show the progress bar
            progressBar.setIndeterminate(true); // Set the ProgressBar to indeterminate mode
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN); // Set the color of the ProgressBar
            // Disable the button
            barangaySpinner.setEnabled(false);
            barangaySpinner.setVisibility(View.GONE); // Show the progress bar
            locationImageView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];
            saveCoordinates(latitude, longitude);
            Log.d("MainActivty", "ConvertCoordinatesTask - Passed latitude value: " + latitude);
            Log.d("MainActivty", "ConvertCoordinatesTask - Passed longitude value: " + longitude);
            String barangay = null;
            int loopCount = 0; // Counter variable for loop iterations

            try {
                double adjustment = LATITUDE_ADJUSTMENT;
                while (barangay == null) {
                    loopCount++; // Increment loop counter

                    String url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=" + latitude + "&lon=" + longitude;
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONObject addressObject = jsonObject.getJSONObject("address");
                        String village = addressObject.optString("village");
                        if (village != null && !village.isEmpty()) {
                            barangay = addressObject.optString("village");
                            Log.d("MainActivty", "ConvertCoordinatesTask - User barangay using OpenStreets: " + barangay);
                        } else if (barangay == null) {
                            // Call the method to load barangays from CSV and get the corresponding barangay name
                            barangay = loadBarangaysFromCSV(latitude, longitude);
                            Log.d("MainActivty", "ConvertCoordinatesTask - loadBarangaysFromCSV has started");
                        } else {
                            Log.d("MainActivty", "ConvertCoordinatesTask - Adjusting coordinates");
                            // Adjust the latitude and longitude based on the current adjustment value
                            latitude += adjustment;

                            // Toggle the adjustment direction
                            if (adjustment > 0) {
                                adjustment = -adjustment;
                            } else {
                                adjustment = -adjustment + LATITUDE_ADJUSTMENT;
                                longitude += LONGITUDE_ADJUSTMENT;
                            }
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Log.d("MainActivty", "ConvertCoordinatesTask - Loop count: " + loopCount); // Log the loop count

            return barangay;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Update the location address in the UI
                barangaySpinner.setText(result);
                Log.d("MainActivty", "ConvertCoordinatesTask - Final detected barangay: " + barangaySpinner); // Log the loop count
            } else {
                // If the barangay is not found using OpenStreets and CSV,
                // set an appropriate message or handle it as needed
                barangaySpinner.setText("Barangay not found");
            }
            progressBar.setVisibility(View.GONE); // Hide the progress bar
            // Enable the button
            barangaySpinner.setEnabled(true);
            barangaySpinner.setVisibility(View.VISIBLE); // Show the progress bar
            locationImageView.setVisibility(View.VISIBLE);
        }
    }

    private String loadBarangaysFromCSV(double sampleLatitude, double sampleLongitude) {
        try {
            // Open the CSV file from the assets folder
            InputStream inputStream = requireContext().getAssets().open("Barangays.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            Log.e("IDUNNO", "loadBarangaysFromCSV - Passed Latitudes value: " + sampleLatitude);
            Log.e("IDUNNO", "loadBarangaysFromCSV - Passed Longitude value: " + sampleLongitude);

            // Round off the sampleLatitude and sampleLongitude to their nearest third decimal value
            sampleLatitude = roundToNearestThirdDecimal(sampleLatitude);
            sampleLongitude = roundToNearestThirdDecimal(sampleLongitude);

            Log.e("IDUNNO", "loadBarangaysFromCSV - Passed Latitude value: " + String.format("%.3f", sampleLatitude));
            Log.e("IDUNNO", "loadBarangaysFromCSV - Passed Longitude value: " + String.format("%.3f", sampleLongitude));

            // Read each line of the CSV file
            String line;
            String correspondingBarangayName = ""; // Variable to store the corresponding barangay name

            while ((line = reader.readLine()) != null) {
                // Split the line by comma
                String[] barangayData = line.split(",");

                // Get the latitude and longitude values from the CSV
                double latitude = Double.parseDouble(barangayData[12]);
                double longitude = Double.parseDouble(barangayData[11]);

                // Check if the latitude and longitude values match the sample location
                if (latitude == sampleLatitude && longitude == sampleLongitude) {
                    // Sample location found
                    correspondingBarangayName = barangayData[4];
                    Log.e("IDUNNO", "loadBarangaysFromCSV - Corresponding Barangay name: " + correspondingBarangayName);
                    break; // Exit the loop since we found the sample location
                }
            }

            // Close the reader
            reader.close();

            return correspondingBarangayName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // Return an empty string if no corresponding barangay name is found
    }

    private double roundToNearestThirdDecimal(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private void loadUserLocation() {
        // Create and add the marker to the map if the location is not Philippines
        if (setLatitude != phLatitude || setLongitude != phLongitude) {
            GeoPoint userLocation = new GeoPoint(setLatitude, setLongitude);
            userMarker = new Marker(mapView);
            userMarker.setPosition(userLocation);
            mapView.getOverlays().add(userMarker);

            // Animate to the user's location with zoom
            mapView.getController().animateTo(userLocation, zoomLevel, null);

            Log.e("Step1Fragment", "onCreateView - stored Latitude value: " + setLatitude);
            Log.e("Step1Fragment", "onCreateView - stored Longitude value: " + setLongitude);
        } else {
            // Create and add the marker to the map
            GeoPoint userLocation = new GeoPoint(setLatitude, setLongitude);
            // Animate to the user's location with zoom
            mapView.getController().animateTo(userLocation, zoomLevel, null);

            Log.e("Step1Fragment", "onCreateView - userLocation Latitude value: " + setLatitude);
            Log.e("Step1Fragment", "onCreateView - userLocation Longitude value: " + setLongitude);

        }
    }

    private void removeUserMarker() {
        // Remove the previous marker from the map
        if (userMarker != null) {
            mapView.getOverlays().remove(userMarker);
            mapView.invalidate();
            userMarker = null;
            Log.d("IDUNNO", "User Marker was removed");
        }
    }

    private void stopLocationUpdates() {
        // Stop location updates
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void showLocationBottomSheet() {
        // Check if the fragment is already visible
        Fragment existingFragment = requireActivity().getSupportFragmentManager().findFragmentByTag("location_bottom_sheet_fragment");
        if (existingFragment != null && existingFragment.isVisible()) {
            // Fragment is already visible, do not show another instance
            return;
        }

        // Create a new instance of the LocationBottomSheetFragment
        LocationBottomSheetFragment fragment = LocationBottomSheetFragment.newInstance(userData, latitude, longitude);
        Log.d("IDUNNO", "showLocationBottomSheet - New value of Latitudess :" + latitude);
        Log.d("IDUNNO", "showLocationBottomSheet - New value of Longitudes :" + longitude);
        // Set the LocationSelectionListener on the fragment (which is IDUNNO)
        fragment.setLocationSelectionListener(this);

        // Show the fragment using a FragmentManager
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, "location_bottom_sheet_fragment");
    }

    private void showBottomSheetDialog() {
        // Create a bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        // Inflate the bottom sheet dialog content view
        View contentView = getLayoutInflater().inflate(R.layout.reporting_barangay_bottom_sheet_dialog_content, null);
        bottomSheetDialog.setContentView(contentView);

        // Find the ListView in the content view
        ListView barangayListView = contentView.findViewById(R.id.barangayListView);

        // Create a custom adapter for the ListView
        CustomDropdownAdapter adapter = new CustomDropdownAdapter(requireContext(), barangayOptions);
        barangayListView.setAdapter(adapter);

        // Set the item click listener for list items
        barangayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBarangay = barangayOptions.get(position);
                // Handle the selected barangay as desired
                barangaySpinner.setText(selectedBarangay);
                userData.setSelectedBarangay(barangaySpinner.getText().toString());
                Log.d("IDUNNO", "showBottomSheetDialog - Chosen Barangay :" + barangaySpinner);
                userData.setLocationEnabled(false);
                setLocationButton.setChecked(false);
                isAffectedByOtherFunction = true;
                bottomSheetDialog.dismiss(); // Dismiss the bottom sheet dialog

                if (!selectedBarangay.isEmpty()) {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(selectedBarangay + ", Lubao, Pampanga, Philippines", 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            latitude = address.getLatitude();
                            longitude = address.getLongitude();
                            // Clear previous markers from the map
                            mapView.getOverlays().clear();

                            // Update the map view to the searched location
                            mapView.getController().animateTo(new GeoPoint(latitude, longitude));
                            saveCoordinates(latitude, longitude);

                            // Add a marker at the searched location
                            userMarker = new Marker(mapView);
                            userMarker.setPosition(new GeoPoint(latitude, longitude));
                            mapView.getOverlays().add(userMarker);

                            // Update the locationTextView with the searched address
                            locationTextView.setText(address.getAddressLine(0));
                            userData.setCrimeExactLocation(locationTextView.getText().toString());
                            // Animate to the searched location with zoom
                            final double zoomLevel = 13.5; // Set your desired zoom level as a double
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
        });

        // Show the bottom sheet dialog
        bottomSheetDialog.show();
    }

    //END OF LOCATION METHODS

    //START OF PERSON METHODS
    private void updateButtonUI() {
        // Check the isYesButtonSelected state from UserData and update the UI accordingly
        if (userData.isYesButtonSelected()) {
            yesButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
            noButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);
            noButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            yesButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            animatedLayout.setVisibility(View.VISIBLE);
        } else {
            noButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
            yesButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);
            yesButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            noButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            animatedLayout.setVisibility(View.GONE);
        }
    }
    //END OF PERSON METHODS

    //START OF DATE METHODS
    private void animateButtonsToLeft() {
        Log.d("IDUNNO", "animateButtonsToLeft - has started");

        int translateDistance = -buttonLayout.getWidth();
        Animation animation = new TranslateAnimation(0, translateDistance, 0, 0);
        animation.setDuration(500); // Adjust the duration to make the animation smoother

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Update the button texts with the corresponding date
                showTextViewFromRight(formattedDate);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Hide the buttons after the animation is complete
                todayButton.setVisibility(View.GONE);
                yesterdayButton.setVisibility(View.GONE);
                selectDateButton.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        todayButton.startAnimation(animation);
        yesterdayButton.startAnimation(animation);
        selectDateButton.startAnimation(animation);
    }

    private void showTextViewFromRight(String date) {
        Log.d("IDUNNO", "showTextViewFromRight - has started");
        // Set the text for the TextView
        if (userData != null) {
            String crimeDate = userData.getCrimeDate();
            if (crimeDate != null) {
                date = userData.getCrimeDate();
            }
        }
        dateTextView.setText(date);

        // Update the visibility of the TextView
        dateTextView.setVisibility(View.VISIBLE);
        iconImageView.setVisibility(View.VISIBLE);

        // Animate the TextView from the right side
        Animation animation = new TranslateAnimation(buttonLayout.getWidth(), 0, 0, 0);
        animation.setDuration(500); // Adjust the duration to make the animation smoother
        dateTextView.startAnimation(animation);
    }

    public void onDateIconClick() {
        Log.d("IDUNNO", "onIconClick - has started");
        // Animate the dateTextView and iconImageView to the right
        animateTextViewAndIconToRight();

    }

    private void animateTextViewAndIconToRight() {
        Log.d("IDUNNO", "animateTextViewAndIconToRight - has started");
        int translateDistance = buttonLayout.getWidth();
        Animation animation = new TranslateAnimation(0, translateDistance, 0, 0);
        animation.setDuration(500); // Adjust the duration to make the animation smoother

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animateButtonsFromLeftToRight();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Hide only the dateTextView after the animation is complete
                dateTextView.setVisibility(View.GONE);
                iconImageView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        dateTextView.startAnimation(animation);

        // Hide the iconImageView immediately without animation
        iconImageView.setVisibility(View.GONE);
    }

    private void animateButtonsFromLeftToRight() {
        Log.d("IDUNNO", "animateButtonsFromLeftToRight - has started");
        Animation animation = new TranslateAnimation(-buttonLayout.getWidth(), 0, 0, 0);
        animation.setDuration(500); // Adjust the duration to make the animation smoother

        todayButton.setVisibility(View.VISIBLE);
        yesterdayButton.setVisibility(View.VISIBLE);
        selectDateButton.setVisibility(View.VISIBLE);

        todayButton.startAnimation(animation);
        yesterdayButton.startAnimation(animation);
        selectDateButton.startAnimation(animation);
    }

    private void showDatePicker() {
        Log.d("IDUNNO", "showDatePicker - has started");
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Create a SimpleDateFormat to format the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

                // Create a Calendar instance and set it to the selected date
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // Format the selected date using the SimpleDateFormat
                formattedDate = dateFormat.format(selectedDate.getTime());
                userData.setCrimeDate(formattedDate);

                // Animate the buttons
                animateButtonsToLeft();

                // Show the iconImageView
                iconImageView.setVisibility(View.VISIBLE);
                Log.d("IDUNNO", "showDatePicker - Selected Date: " + formattedDate);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTextViewNoAnimation(String date) {
        Log.d("IDUNNO", "showTextViewFromRight - has started");
        // Set the text for the TextView
        if (userData != null) {
            String crimeDate = userData.getCrimeDate();
            if (crimeDate != null) {
                date = userData.getCrimeDate();
            }
        }
        dateTextView.setText(date);

        // Update the visibility of the TextView
        dateTextView.setVisibility(View.VISIBLE);
        iconImageView.setVisibility(View.VISIBLE);
        todayButton.setVisibility(View.GONE);
        yesterdayButton.setVisibility(View.GONE);
        selectDateButton.setVisibility(View.GONE);
    }

    private void updateDateButtons(String crimeDate) {
        // Parse the stored date and create a Calendar instance
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        Calendar selectedDate = Calendar.getInstance();
        try {
            selectedDate.setTime(dateFormat.parse(crimeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get the current date for comparison
        Calendar currentDate = Calendar.getInstance();

        // Check if the stored date is today
        if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)) {
            // Set the Today button as selected
            todayButton.setSelected(true);
            yesterdayButton.setSelected(false);
            selectDateButton.setSelected(false);

            // Update the text colors and backgrounds for the buttons accordingly
            todayButton.setTextColor(getResources().getColor(R.color.selected_text_color));
            yesterdayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
            selectDateButton.setTextColor(getResources().getColor(R.color.unselected_text_color));

            todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));
            yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
            selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
        } else {
            // Calculate the previous date for comparison
            Calendar previousDate = Calendar.getInstance();
            previousDate.add(Calendar.DAY_OF_MONTH, -1);

            // Check if the stored date is yesterday
            if (selectedDate.get(Calendar.YEAR) == previousDate.get(Calendar.YEAR) &&
                    selectedDate.get(Calendar.DAY_OF_YEAR) == previousDate.get(Calendar.DAY_OF_YEAR)) {
                // Set the Yesterday button as selected
                todayButton.setSelected(false);
                yesterdayButton.setSelected(true);
                selectDateButton.setSelected(false);

                // Update the text colors and backgrounds for the buttons accordingly
                todayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.selected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.unselected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
            } else {
                // The stored date is neither today nor yesterday, set the Select Date button as selected
                todayButton.setSelected(false);
                yesterdayButton.setSelected(false);
                selectDateButton.setSelected(true);

                // Update the text colors and backgrounds for the buttons accordingly
                todayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                yesterdayButton.setTextColor(getResources().getColor(R.color.unselected_text_color));
                selectDateButton.setTextColor(getResources().getColor(R.color.selected_text_color));

                todayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                yesterdayButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_unselected_button_background));
                selectDateButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.reporting_date_button_selected_shape));

                showTextViewNoAnimation(crimeDate);
            }
        }
    }
    //END OF DATE METHODS

    //START OF TIME METHODS
    private void applyClickAnimation(@AnimatorRes int animationResId, View view) {
        Animator animator = AnimatorInflater.loadAnimator(requireContext(), animationResId);
        animator.setTarget(view);
        animator.start();
    }

    // To get the selected time, you can use the following method
    private String getSelectedTime() {
        int hour = Integer.parseInt(hourEditText.getText().toString());
        int minute = Integer.parseInt(minuteEditText.getText().toString());
        String time = String.format("%02d:%02d", hour, minute);
        String timeIndication;
        if (pmTextView.getCurrentTextColor() == prmyClr) {
            // PM selected
            time += " PM";
            timeIndication = "PM";
        } else {
            // AM selected
            time += " AM";
            timeIndication = "AM";
        }
        Log.d("IDUNNO", "getSelectedTime - Selected Time: " + time);
        Log.d("IDUNNO", "getSelectedTime - Selected Hour: " + hour);

        // Update the UserData object with the selected time
        userData.setCrimeHour(hour);
        userData.setCrimeMinute(minute);
        userData.setCrimeTimeIndication(timeIndication);

        return time;
    }
    //END OF TIME METHODS

    @Override
    public void onResume() {
        super.onResume();
        updateButtonUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates when the fragment is paused
        stopLocationUpdates();
    }

    //START OF IMAGE METHODS
    private void displayImages() {
        Log.d("IDUNNO", "displayImages - has started");
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Clear the imageContainer before adding the new images
                imageContainer.removeAllViews();

                for (final String imageUrl : userData.getSelectedImageUrls()) {
                    // Create a new FrameLayout to hold the ImageView and delete button
                    FrameLayout imageLayout = new FrameLayout(requireActivity());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(
                            getResources().getDimensionPixelSize(R.dimen.image_left_margin),
                            getResources().getDimensionPixelSize(R.dimen.image_top_margin),
                            getResources().getDimensionPixelSize(R.dimen.image_right_margin),
                            getResources().getDimensionPixelSize(R.dimen.image_bot_margin)
                    );
                    imageLayout.setLayoutParams(layoutParams);

                    // Create a new ImageView for the image
                    ImageView imageView = new ImageView(requireActivity());
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            getResources().getDimensionPixelSize(R.dimen.image_width),
                            getResources().getDimensionPixelSize(R.dimen.image_height)
                    );
                    imageView.setLayoutParams(imageParams);

// Set ScaleType to FIT_XY
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    // Load the image using your preferred library (e.g., Picasso, Glide, etc.)
                    // Example with Picasso:
                    Picasso.get()
                            .load(imageUrl)
                            .fit()
                            .centerCrop()
                            .into(imageView);

// Create a new ImageView for the delete button
                    ImageView deleteButton = new ImageView(requireActivity());
                    FrameLayout.LayoutParams deleteButtonParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonParams.gravity = Gravity.TOP | Gravity.END; // Position in top right corner
                    deleteButton.setLayoutParams(deleteButtonParams);
                    deleteButton.setImageResource(R.drawable.ic_image_delete); // Set your delete icon drawable here

                    final int imageIndex = userData.getSelectedImageUrls().indexOf(imageUrl); // Get the index of the image to delete

// Set a click listener for the delete button
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if the image index is valid
                            if (imageIndex >= 0 && imageIndex < userData.getSelectedImageUrls().size()) {
                                // Remove the image URL from the list using the index
                                userData.getSelectedImageUrls().remove(imageIndex);
                                // Redisplay the updated images
                                displayImages();
                            }
                        }
                    });

                    // Add the ImageView and delete button to the imageLayout
                    imageLayout.addView(imageView);
// Add the ImageView (delete button) to the imageLayout
                    imageLayout.addView(deleteButton);
                    // Add the imageLayout to the imageContainer
                    imageContainer.addView(imageLayout);
                }
            }
        });
    }

    private void openImagePicker() {
        Log.d("IDUNNO", "openImagePicker - has started");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST_CODE);
    }

    //END OF IMAGE METHODS
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                // Clear the selectedImageUrls list before adding new URLs
                userData.getSelectedImageUrls().clear();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Process the imageUri and upload the image to your server
                    // Obtain the URL of the uploaded image and add it to the selectedImageUrls list
                    // Example:
                    String imageUrl = imageUri.toString();
                    userData.getSelectedImageUrls().add(imageUrl);
                    validateUserInputs();
                    Log.d("IDUNNO", "onActivityResult - FIrst condition - Image URL: " + imageUrl); // Log the image URL
                }
            } else if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                // Process the imageUri and upload the image to your server
                // Obtain the URL of the uploaded image and add it to the selectedImageUrls list
                // Example:
                String imageUrl = imageUri.toString();
                userData.getSelectedImageUrls().add(imageUrl);
                validateUserInputs();
                Log.d("IDUNNO", "onActivityResult - Second condition - Image URL: " + imageUrl); // Log the image URL
            }
            // Display the selected images using the userData.getSelectedImageUrls() list
            displayImages();
        } else {
            // Log an error message if the result code or request code doesn't match
            Log.e("IDUNNO", "onActivityResult - Failed to pick images. Result code: " + resultCode + ", Request code: " + requestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, request location updates
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            // Permission denied, handle accordingly
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // The user has denied the permission but hasn't selected "Don't ask again"
                // You can show a dialog or message explaining why the permission is needed and request it again
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                // The user has denied the permission and selected "Don't ask again"
                // You can show a dialog or message informing the user that the permission is required and guide them to the app settings to enable it manually
                Toast.makeText(requireActivity(), "Location permission denied. Please enable it in the app settings.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize location manager and listener
                initLocationManager();
            } else {
                // Permission denied, handle accordingly (show a message, etc.)
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to navigate back to Step1Fragment
    private void navigateToStep1Fragment() {
        saveUserData();
        // Navigate to the previous fragment (Step1Fragment)
        ((createReport_activity) requireActivity()).navigateToPreviousFragment(new Step1Fragment());
    }
}