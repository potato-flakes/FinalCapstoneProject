package com.system.finalcapstoneproject.reportingsystem;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.system.finalcapstoneproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class editReport_activity extends AppCompatActivity {

    private int currentPage = 0; // Start from the first page
    private ProgressBar userProgressBar;
    private TextView textViewProgress;
    private TextView progressLabel;
    private TextView typeLabel;
    private TextView reportNumber;
    private ViewGroup containerLayout;
    private View loadingView;
    private AlertDialog dialog;
    private Handler handler = new Handler();
    private UserData userData;
    private String reportId;
    ArrayList<String> existingImageUrls = new ArrayList<>();
    private String GET_REPORT_DETAILS = UrlConstants.GET_REPORT_DETAILS;
    private String GET_REPORT_IMAGES = UrlConstants.GET_REPORT_IMAGES;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporting_edit_report_layout);

        userData = new UserData();

        // Retrieve the reportId from the intent
        reportId = getIntent().getStringExtra("reportId");
        Log.e("Report ID", reportId);

        userData.setReportID(reportId);

        // Retrieve the existing report details based on the reportId
        retrieveReportDetailsFromServer(reportId);

        reportNumber = findViewById(R.id.reportNumber);
        userProgressBar = findViewById(R.id.userProgressBar);
        textViewProgress = findViewById(R.id.text_view_progress);
        progressLabel = findViewById(R.id.progressLabel);
        typeLabel = findViewById(R.id.typeLabel);
        containerLayout = findViewById(R.id.progress_container);
        loadingView = getLayoutInflater().inflate(R.layout.reporting_loading_screen, containerLayout, false);
        showLoadingScreen();
        // Add a delay before starting the first fragment
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextFragment(new EditReportStep1Fragment());
                hideLoadingScreen();
            }
        }, 1000);
    }

    public void navigateToNextFragment(Fragment fragment) {
        Log.e("editReport_activity", "navigateToNextFragment before: " + currentPage);
        // Pass the userData to the next fragment
        if (fragment instanceof EditReportStep1Fragment) {
            ((EditReportStep1Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof EditReportStep2Fragment) {
            ((EditReportStep2Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof EditReportStep3Fragment) {
            ((EditReportStep3Fragment) fragment).setUserData(userData);
        }
        currentPage++;
        updateProgress(currentPage * 33); // Update progress with animation
        updateProgressText(currentPage + "/3");
        updateLabels(); // Update labels here
        Log.e("editReport_activity", "navigateToNextFragment after: " + currentPage);
        switch (currentPage) {
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                Log.e("editReport_activity", "navigateToNextFragment - This is Case 1");
                break;
            case 2:
            case 3:
                Log.e("editReport_activity", "navigateToNextFragment - This is Case 2 or 3");
                showLoadingScreen();
                handler.postDelayed(() -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    hideLoadingScreen();
                }, 1500);
                break;
            default:
                // Form completed
                showFormComplete();
                break;
        }
    }

    public void navigateToPreviousFragment(Fragment fragment) {
        // Decrement currentPage here
        Log.e("editReport_activity", "navigateToPreviousFragment before: " + currentPage);
        currentPage--;
        Log.e("editReport_activity", "navigateToPreviousFragment after: " + currentPage);
        // Pass the userData to the previous fragment
        if (fragment instanceof EditReportStep1Fragment) {
            ((EditReportStep1Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof EditReportStep2Fragment) {
            ((EditReportStep2Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof EditReportStep3Fragment) {
            ((EditReportStep3Fragment) fragment).setUserData(userData);
        }

        // Update progress, labels, and replace the fragment
        int progress = currentPage * 33;
        updateProgress(progress);
        updateProgressText(currentPage + "/3");
        updateLabels(); // Update labels here

        // Replace the fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        hideLoadingScreen();
    }

    private void updateLabels() {
        // Update progressLabel and typeLabel based on currentPage
        switch (currentPage) {
            case 1:
                progressLabel.setText("Next: Crime Details");
                typeLabel.setText("Crime Type");
                break;
            case 2:
                progressLabel.setText("Next: Personal Details");
                typeLabel.setText("Crime Details");
                break;
            case 3:
                progressLabel.setText("Next: Summary Details");
                typeLabel.setText("Personal Details");
                break;
            default:
                break;
        }
    }

    private void showLoadingScreen() {
        containerLayout.addView(loadingView);
    }

    private void hideLoadingScreen() {
        containerLayout.removeView(loadingView);
    }

    // Method to update the progress text
    private void updateProgressText(String progressText) {
        textViewProgress.setText(progressText);
    }

    // Method to update the progress bar
    private void updateProgress(int progress) {
        // Get the current progress value
        int currentProgress = userProgressBar.getProgress();

        // Create an ObjectAnimator to animate the progress change
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(userProgressBar, "progress", currentProgress, progress);
        progressAnimator.setDuration(1100); // Set the animation duration in milliseconds (adjust as needed)
        // Start the progress animation
        progressAnimator.start();
    }

    // Method to handle form completion
    void showFormComplete() {
        // For example, show a success message
        Toast.makeText(this, "Form completed successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Clear the userData object when canceling the report
        userData = new UserData();
        // Create a custom dialog with the custom theme
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.reporting_back_warning, null);
        builder.setView(dialogView);

        // Get references to the buttons in the custom dialog layout
        Button btnGoBack = dialogView.findViewById(R.id.btnGoBack);
        Button btnCancelReport = dialogView.findViewById(R.id.btnCancelReport);

        // Set click listeners for the buttons
        btnGoBack.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the dialog when "Go Back" button is clicked
        });

        btnCancelReport.setOnClickListener(v -> {
            // Dismiss the dialog when "Cancel Report" button is clicked
            // Call the superclass method to handle "Go Back"
            super.onBackPressed();
            dialog.dismiss();
        });

        // Create and show the dialog
        dialog = builder.create();
        dialog.show();
    }

    private void retrieveReportDetailsFromServer(final String reportId) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Retrieve the report details from the server using the reportId
                // Make an HTTP request to the server and parse the response JSON to populate userData
                Log.e("editReportActivity", "retrieveReportDetailsFromServer - has started"); // Debug log
                // Example of retrieving report details from the server

                // Perform the HTTP request and retrieve the response
                String jsonResponse = performHttpRequest(GET_REPORT_DETAILS + reportId);

                // Log the JSON response
                Log.d("JSON Response", jsonResponse);

                // Parse the JSON and populate userData
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String report_id = jsonObject.getString("report_id");
                    String user_id = jsonObject.getString("user_id");
                    String crime_type = jsonObject.getString("crime_type");
                    int isIdentified = jsonObject.getInt("isIdentified");
                    String crime_person = jsonObject.getString("crime_person");
                    String crime_location = jsonObject.getString("crime_location");
                    String crime_barangay = jsonObject.getString("crime_barangay");
                    int isUseCurrentLocation = jsonObject.getInt("isUseCurrentLocation");
                    String crime_date = jsonObject.getString("crime_date");

                    // Define the date format for the database date
                    SimpleDateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    // Define the desired output date format
                    SimpleDateFormat desiredDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                    String formattedDate = null;
                    try {
                        // Parse the database date string into a Date object
                        Date date = databaseDateFormat.parse(crime_date);

                        // Format the Date object into the desired format
                        formattedDate = desiredDateFormat.format(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String crime_time = jsonObject.getString("crime_time");
                    String[] timeParts = crime_time.split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    String timeIndication;

                    if (hour >= 12) {
                        timeIndication = "PM";
                        if (hour > 12) {
                            hour -= 12;
                        }
                    } else {
                        timeIndication = "AM";
                        if (hour == 0) {
                            hour = 12;
                        }
                    }

                    Double crime_location_latitude = Double.valueOf(jsonObject.getString("crime_location_latitude"));
                    Double crime_location_longitude = Double.valueOf(jsonObject.getString("crime_location_longitude"));
                    String crime_description = jsonObject.getString("crime_description");

                    String crime_user_name = jsonObject.getString("crime_user_name");

                    // Split the user name into first name and last name
                    String[] nameParts = crime_user_name.split(" ");
                    if (nameParts.length > 0) {
                        // The first part is the first name
                        userData.setUserFirstName(nameParts[0]);

                        // The rest of the parts (if any) are the last name
                        if (nameParts.length > 1) {
                            StringBuilder lastName = new StringBuilder();
                            for (int i = 1; i < nameParts.length; i++) {
                                lastName.append(nameParts[i]);
                                if (i < nameParts.length - 1) {
                                    lastName.append(" "); // Add spaces between last name parts
                                }
                            }
                            userData.setUserLastName(lastName.toString());
                        }
                    }


                    String crime_user_sex = jsonObject.getString("crime_user_sex");
                    String crime_user_phone = jsonObject.getString("crime_user_phone");
                    String crime_user_email = jsonObject.getString("crime_user_email");
                    String report_date = jsonObject.getString("report_date");

                    reportNumber.setText("Report ID: " + report_id);

                    userData.setCrimeHour(hour);
                    userData.setCrimeMinute(minute);
                    userData.setCrimeTimeIndication(timeIndication);

                    userData.setCrimeType(crime_type);

                    if (isIdentified == 1) {
                        userData.setYesButtonSelected(true);
                    } else {
                        userData.setYesButtonSelected(false);
                    }

                    userData.setCrimePerson(crime_person);
                    userData.setSelectedBarangay(crime_barangay);

                    if (isUseCurrentLocation == 1) {
                       userData.setLocationEnabled(true);
                    } else {
                        userData.setLocationEnabled(false);
                    }

                    Log.d("editReport_activity", "onCreateView - Switch status: " + isUseCurrentLocation);

                    userData.setCrimeExactLocation(crime_location);
                    userData.setCrimeDate(formattedDate);

                    userData.setCrimeLatitude(crime_location_latitude);
                    userData.setCrimeLongitude(crime_location_longitude);
                    userData.setCrimeDescription(crime_description);

                    userData.setUserSex(crime_user_sex);
                    userData.setUserPhone(crime_user_phone);
                    userData.setUserEmail(crime_user_email);

                    if (jsonObject.has("imagePaths")) {

                        // Retrieve the image paths associated with the report ID
                        JSONArray imagePathsArray = jsonObject.getJSONArray("imagePaths");
                        for (int i = 0; i < imagePathsArray.length(); i++) {
                            String imagePath = imagePathsArray.getString(i);
                            String imageUrl = GET_REPORT_IMAGES + imagePath; // Modify the URL as per your server setup
                            userData.getSelectedImageUrls().add(imageUrl);
                            // Add the imageUrl to existingImageUrls
                            existingImageUrls.add(imageUrl);
                            userData.setExistingImageUrls(existingImageUrls);

                            Log.d("Retrieved Images: ", imagePath); // Debug log to check the JSON response
                            Log.e("editReportActivity", "retrieveReportDetailsFromServer - Retrieved Images: " + existingImageUrls); // Debug log to check the JSON response
                        }

                    } else {
                        Log.e("JSON", "No value for imagePaths key");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON", "Error parsing JSON: " + e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Handle any post-execution tasks here, if needed
                // For example, you can update the UI with the retrieved data
            }
        };

        task.execute();
    }

    private String performHttpRequest(String apiUrl) {
        // Perform the HTTP request to the server and retrieve the response
        // You can use a networking library like Retrofit, Volley, or OkHttp to simplify the HTTP request process
        // In this example, we'll use HttpURLConnection for simplicity

        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("editReport_activity", "performHttpRequest - Server Response: " + response);
            Toast.makeText(editReport_activity.this, "Failed to retrieve report details", Toast.LENGTH_SHORT).show();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return response.toString();
    }
}
