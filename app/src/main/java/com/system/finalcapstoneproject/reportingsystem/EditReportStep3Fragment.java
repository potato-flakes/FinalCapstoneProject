package com.system.finalcapstoneproject.reportingsystem;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.system.finalcapstoneproject.R;
import com.system.finalcapstoneproject.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EditReportStep3Fragment extends Fragment {

    private TextView typeOfCrimeTextView;
    private TextView dateOfCrimeTextView;
    private TextView suspectOfCrimeTextView;
    private TextView evidencesOfCrimeTextView;
    private EditText descOfCrimeEditText;
    private TextView nameTextView;
    private TextView sexTextView;
    private TextView bdayTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button maleButton;
    private Button femaleButton;
    private Button nextButton;
    private UserData userData;
    private RelativeLayout backButtonToF1;
    private RelativeLayout backButtonToF3;
    private AlertDialog summaryDialog;
    private Button btnCancelReport;
    private Button btnSendReport;
    private String UPDATE_REPORT = UrlConstants.UPDATE_REPORT;
    private String UPLOAD_IMAGES_REPORT = UrlConstants.UPLOAD_IMAGES_REPORT;
    private Handler handler = new Handler();
    private List<String> imageUrls = new ArrayList<>();
    private boolean dataFetched = false;
    // Access existingImageUrls from UserData
    ArrayList<String> existingUrls;

    // Access deletedImageUrls from UserData
    private List<String> deletedImageUrls = new ArrayList<>();

    // Access newImageUrls from UserData
    ArrayList<String> newUrls;
    private ProgressBar progressBar;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }


    public EditReportStep3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reporting_fragment_step3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("EditReportStep3Fragment", "You are in EditReportStep3Fragment");

        firstNameEditText = view.findViewById(R.id.editTextFirstName);
        lastNameEditText = view.findViewById(R.id.editTextLastName);
        emailEditText = view.findViewById(R.id.editTextEmail);
        phoneEditText = view.findViewById(R.id.editTextPhone);
        nextButton = view.findViewById(R.id.nextButton);

        // Replace "your-server-url.com" with the actual URL of your server and PHP script
        String serverUrl = "http://192.168.100.228/recyclearn/report_user/get_user_details.php";
        String userId = "5320007"; // Replace this with the actual user ID you want to fetch

        // Find the Yes and No buttons
        maleButton = view.findViewById(R.id.maleButton);
        femaleButton = view.findViewById(R.id.femaleButton);

        // Change the background color of the Yes button to colorPrimary
        maleButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
        // Revert the background color of the No button to the default color
        femaleButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

        // Change the text color of the No button to colorPrimary
        femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        // Revert the text color of the Yes button to the default color
        maleButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        // Set click listeners for the buttons
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the background color of the Yes button to colorPrimary
                maleButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
                // Revert the background color of the No button to the default color
                femaleButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

                // Change the text color of the No button to colorPrimary
                femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                // Revert the text color of the Yes button to the default color
                maleButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                // Apply the click animation
                applyClickAnimation(R.animator.reporting_button_scale, v);
                userData.setUserSex("Male");
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the background color of the No button to colorPrimary
                femaleButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
                // Revert the background color of the Yes button to the default color
                maleButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);

                // Change the text color of the Yes button to colorPrimary
                maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                // Revert the text color of the No button to the default color
                femaleButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                // Apply the click animation
                applyClickAnimation(R.animator.reporting_button_scale, v);
                userData.setUserSex("Female");
            }
        });

        // Set the user details in the EditText fields
        firstNameEditText.setText(userData.getUserFirstName());
        lastNameEditText.setText(userData.getUserLastName());
        emailEditText.setText(userData.getUserEmail());
        phoneEditText.setText(userData.getUserPhone());

        // Set the gender button based on the fetched gender
        String sex = userData.getUserSex();
        if ("Male".equalsIgnoreCase(sex)) {
            // Set Male button as selected
            maleButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
            maleButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            // Reset Female button
            femaleButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);
            femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        } else if ("Female".equalsIgnoreCase(sex)) {
            // Set Female button as selected
            femaleButton.setBackgroundResource(R.drawable.reporting_yes_toggle_background);
            femaleButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            // Reset Male button
            maleButton.setBackgroundResource(R.drawable.reporting_person_no_button_selector);
            maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        }

        Button backButton = view.findViewById(R.id.backButton);
        // Inside EditReportStep2Fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserInfo();
                ((editReport_activity) requireActivity()).navigateToPreviousFragment(new EditReportStep2Fragment());
            }
        });

        // Inside EditReportStep3Fragment.java
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserInfo();
                showSummaryDialog();
            }
        });
        existingUrls = userData.getExistingImageUrls();
        newUrls = userData.getNewImageUrls();
        deletedImageUrls = userData.getDeletedImageUrls();
    }

    private void loadUserInfo() {
        String storeFirstName = firstNameEditText.getText().toString();
        String storeLastName = lastNameEditText.getText().toString();
        String storeEmail = emailEditText.getText().toString();
        String storePhone = phoneEditText.getText().toString();

        userData.setUserFirstName(storeFirstName);
        userData.setUserLastName(storeLastName);
        userData.setUserEmail(storeEmail);
        userData.setUserPhone(storePhone);
    }

    private void showSummaryDialog() {
        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.reporting_dialog_summary, null);
        builder.setView(dialogView);

        // Create and show the dialog
        summaryDialog = builder.create();

        typeOfCrimeTextView = dialogView.findViewById(R.id.typeOfCrimeTextView);
        dateOfCrimeTextView = dialogView.findViewById(R.id.dateOfCrimeTextView);
        suspectOfCrimeTextView = dialogView.findViewById(R.id.suspectOfCrimeTextView);
        descOfCrimeEditText = dialogView.findViewById(R.id.descOfCrimeEditText);
        nameTextView = dialogView.findViewById(R.id.nameTextView);
        sexTextView = dialogView.findViewById(R.id.sexTextView);
        phoneTextView = dialogView.findViewById(R.id.phoneTextView);
        emailTextView = dialogView.findViewById(R.id.emailTextView);
        evidencesOfCrimeTextView = dialogView.findViewById(R.id.evidencesOfCrimeTextView);
        progressBar = dialogView.findViewById(R.id.progressBar);
        btnCancelReport = dialogView.findViewById(R.id.btnCancelReport);

        // Set text in TextViews with user data
        typeOfCrimeTextView.setText(userData.getCrimeType());
        dateOfCrimeTextView.setText(userData.getCrimeDate());
        if (!userData.isYesButtonSelected()){
            suspectOfCrimeTextView.setText("Unknown");
            userData.setCrimePerson(suspectOfCrimeTextView.getText().toString());
        } else {
            suspectOfCrimeTextView.setText(userData.getCrimePerson());
        }
        int numberOfImages = userData.getSelectedImageUrls().size();
        evidencesOfCrimeTextView.setText(numberOfImages + " images");
        // Set the text programmatically
        descOfCrimeEditText.setText(userData.getCrimeDescription());

// Disable text editing
        descOfCrimeEditText.setFocusable(false);
        descOfCrimeEditText.setFocusableInTouchMode(false);

// Prevent keyboard input
        descOfCrimeEditText.setKeyListener(null);

// Enable scrolling
        descOfCrimeEditText.setVerticalScrollBarEnabled(true);
        descOfCrimeEditText.setMovementMethod(new ScrollingMovementMethod());
        // Set text in TextViews with user data
        nameTextView.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        sexTextView.setText(userData.getUserSex());
        Log.e("EditReportStep3Fragment", "showSummaryDialog - Sex:: " + sexTextView);
        phoneTextView.setText(userData.getUserPhone());
        emailTextView.setText(userData.getUserEmail());

        backButtonToF1 = dialogView.findViewById(R.id.backButtonToF1);
        backButtonToF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the previous fragment (EditReportStep1Fragment)
                ((editReport_activity) requireActivity()).navigateToPreviousFragment(new EditReportStep2Fragment());
                summaryDialog.dismiss();
                summaryDialog = null; // Reset the dialog reference
            }
        });
// Find the ImageView by its ID
        backButtonToF3 = dialogView.findViewById(R.id.backButtonToF3);

        backButtonToF3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summaryDialog.dismiss();
                summaryDialog = null; // Reset the dialog reference
            }
        });

        btnCancelReport = dialogView.findViewById(R.id.btnCancelReport);

        btnCancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summaryDialog.dismiss();
                summaryDialog = null; // Reset the dialog reference
            }
        });


        btnSendReport = dialogView.findViewById(R.id.btnSendReport);

        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendReport();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        imageUrls = userData.getSelectedImageUrls();
        summaryDialog.show();
    }


    private void sendReport() throws ParseException {
        // Retrieve user ID from the intent or wherever you store it
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar
        progressBar.setIndeterminate(true); // Set the ProgressBar to indeterminate mode
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN); // Set the color of the ProgressBar
        // Disable the button
        btnSendReport.setEnabled(false);
        btnSendReport.setText("");

        final String report_id = userData.getReportID();
        final String user_id = "9183797"; // Replace with the actual user ID
        final String crime_type = userData.getCrimeType();
        final String crime_person = userData.getCrimePerson();
        final String crime_date = userData.getCrimeDate();
        // Convert the input date string to the database format (yyyy-MM-dd)
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date parsedDate = inputDateFormat.parse(crime_date); // Parse the input date
        String formattedDate = dbDateFormat.format(parsedDate); // Format to yyyy-MM-dd

        int hour = userData.getCrimeHour();
        String timeIndicator = userData.getCrimeTimeIndication();

        if ("PM".equals(timeIndicator) && hour != 12) {
            hour += 12;
        } else if (!"PM".equals(timeIndicator) && hour == 12) {
            hour = 0; // Midnight
        }

        int minute = userData.getCrimeMinute();

// Construct the time in HH:MM format for storage in the database
        String formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute);

        final String crime_barangay = userData.getSelectedBarangay();
        final String crime_location = userData.getCrimeExactLocation();
        final Double crime_locationLatitude = userData.getCrimeLatitude();
        final Double crime_locationLongitude = userData.getCrimeLongitude();
// Convert latitude and longitude to compatible format
        Double crime_location_latitude = Double.valueOf(String.format(Locale.US, "%.6f", crime_locationLatitude));
        Double crime_location_longitude = Double.valueOf(String.format(Locale.US, "%.6f", crime_locationLongitude));
        final String crime_description = userData.getCrimeDescription();
        final String crime_user_name = userData.getUserFirstName() + " " + userData.getUserLastName();
        final String crime_user_sex = userData.getUserSex();
        final String crime_user_phone = userData.getUserPhone();
        final String crime_user_email = userData.getUserEmail();
        final boolean switchStatus = userData.isLocationEnabled();
        final boolean isIdentified = userData.isYesButtonSelected();

        Log.e("EditReportStep3Fragment", "sendReport - userId: " + user_id);
        Log.e("EditReportStep3Fragment", "sendReport - crime_type: " + crime_type);
        Log.e("EditReportStep3Fragment", "sendReport - isIdentified: " + isIdentified);
        Log.e("EditReportStep3Fragment", "sendReport - crime_person: " + crime_person);
        Log.e("EditReportStep3Fragment", "sendReport - crime_date: " + formattedDate);
        Log.e("EditReportStep3Fragment", "sendReport - crime_time: " + formattedTime);
        Log.e("EditReportStep3Fragment", "sendReport - crime_location: " + crime_location);
        Log.e("EditReportStep3Fragment", "sendReport - crime_barangay: " + crime_barangay);
        Log.e("EditReportStep3Fragment", "sendReport - switchStatus: " + switchStatus);
        Log.e("EditReportStep3Fragment", "sendReport - crime_locationLatitude: " + crime_location_latitude);
        Log.e("EditReportStep3Fragment", "sendReport - crime_locationLongitude: " + crime_location_longitude);
        Log.e("EditReportStep3Fragment", "sendReport - crime_description: " + crime_description);

        Log.e("EditReportStep3Fragment", "sendReport - crime_userName: " + crime_user_name);
        Log.e("EditReportStep3Fragment", "sendReport - crime_userSex: " + crime_user_sex);
        Log.e("EditReportStep3Fragment", "sendReport - crime_userPhone: " + crime_user_phone);
        Log.e("EditReportStep3Fragment", "sendReport - crime_userEmail: " + crime_user_email);

        Log.e("EditReportStep3Fragment", "sendReport - Existing Images: " + existingUrls);
        Log.e("EditReportStep3Fragment", "sendReport - Deleted Images: " + deletedImageUrls);
        Log.e("EditReportStep3Fragment", "sendReport - New Images: " + newUrls);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Declare response variable outside the try-catch block
                String response = "";
                try {
                    // Create JSON object with crime data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("report_id", report_id);
                    jsonObject.put("user_id", user_id);
                    jsonObject.put("crime_type", crime_type);
                    jsonObject.put("isIdentified", isIdentified);
                    jsonObject.put("crime_person", crime_person);
                    jsonObject.put("crime_date", formattedDate);
                    jsonObject.put("crime_time", formattedTime);
                    jsonObject.put("crime_barangay", crime_barangay);
                    jsonObject.put("isUseCurrentLocation", switchStatus);
                    jsonObject.put("crime_location", crime_location);
                    jsonObject.put("crime_description", crime_description);
                    jsonObject.put("crime_location_latitude", crime_location_latitude);
                    jsonObject.put("crime_location_longitude", crime_location_longitude);

                    jsonObject.put("crime_user_name", crime_user_name);
                    jsonObject.put("crime_user_sex", crime_user_sex);
                    jsonObject.put("crime_user_phone", crime_user_phone);
                    jsonObject.put("crime_user_email", crime_user_email);
                    jsonObject.put("deletedImageUrls", new JSONArray(deletedImageUrls));

                    Log.d("MainActivity", "reportCrime - Data to be passed: " + jsonObject);

                    // Send the data to the PHP API and get the response
                    URL url = new URL(UPDATE_REPORT);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(jsonObject.toString());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response from the API
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();

                        // Assign the response to the declared variable
                        response = responseBuilder.toString();

                        // Parse the response as JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        Log.d("MainActivity", "reportCrime - JSON Response - Data upload: " + jsonResponse);
                        // Extract the report ID from the response
                        Log.d("MainActivity", "reportCrime - Retrieved Report ID from server: " + report_id);

                        // Check if the "deletionStatus" array exists in the response
                        if (jsonResponse.has("deletionStatus")) {
                            JSONArray deletionStatusArray = jsonResponse.getJSONArray("deletionStatus");

                            // Iterate through each deletion status entry
                            for (int i = 0; i < deletionStatusArray.length(); i++) {
                                JSONObject deletionStatusEntry = deletionStatusArray.getJSONObject(i);
                                String imageURL = deletionStatusEntry.getString("imageURL");
                                String status = deletionStatusEntry.getString("status");

                                // Log the deletion status for each image
                                Log.d("MainActivity", "Image URL: " + imageURL + ", Status: " + status);

                                // You can handle the status as needed (e.g., show a message to the user)
                                if ("error".equals(status)) {
                                    // Handle the error case for this image deletion
                                    // For example, show a message to the user or take appropriate action
                                }
                            }
                        }
                        if (newUrls == null || newUrls.isEmpty()) {
                            // Inside your EditReportStep2Fragment
                            Handler handler = new Handler(Looper.getMainLooper());

                            // To run code on the UI thread, use the handler like this:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "Report edited successfully", Toast.LENGTH_SHORT).show();
                                    // Exit the fragment here since newUrls is null or empty
                                    clearForm();
                                }
                            });
                        } else {
                            // Inside your sendReport method
                            uploadImagesToServer(newUrls, report_id);
                        }

                    } else {
                        // Display an error message
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Response from server does not match", Toast.LENGTH_SHORT).show();
                                Log.e("MainActivity", "reportCrime - Check report.php");
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    // Log the response string for debugging
                    Log.e("YourTag", "Response String: " + response); // Use the declared response variable
                    e.printStackTrace();
                    // Display an error message
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Please check the inputted details", Toast.LENGTH_SHORT).show();
                            Log.e("MainActivity", "reportCrime - Check inputted details");
                        }
                    });
                }
            }
        }).start();
    }

    private void uploadImagesToServer(final List<String> imageUrls, final String report_id) {
        Log.d("MainActivity", "uploadImagesToServer - has started");
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = UPLOAD_IMAGES_REPORT;
        final AtomicInteger uploadCounter = new AtomicInteger(0);

        for (final String imageUrl : imageUrls) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("MainActivity", "uploadImagesToServer - Condition: " + response);
                    int count = uploadCounter.incrementAndGet();
                    if (count == imageUrls.size()) {
                        // All images have been uploaded
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.equals("success")) {
                                    Toast.makeText(requireContext(), "All images are successfully uploaded", Toast.LENGTH_SHORT).show();
                                    clearForm();
                                } else {
                                    Toast.makeText(requireContext(), "Some images failed to upload", Toast.LENGTH_SHORT).show();
                                    Log.e("MainActivity", "uploadImagesToServer - Check upload.php");
                                }
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> paramV = new HashMap<>();
                    if (imageUrl != null) {
                        try {
                            Bitmap imageBitmap = getBitmapFromUri(Uri.parse(imageUrl));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageData = baos.toByteArray();

                            String encodedImageData = Base64.encodeToString(imageData, Base64.DEFAULT);
                            paramV.put("images", encodedImageData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    paramV.put("report_id", report_id);
                    Log.d("Upload Image Method:", "ReportID on database: " + report_id);
                    return paramV;
                }
            };

            queue.add(stringRequest);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    private void clearForm() {
        requireActivity().finish();
        summaryDialog.dismiss();
        Log.d("MainActivity", "clearForm - Form Cleared");
    }

    private void applyClickAnimation(@AnimatorRes int animationResId, View view) {
        Animator animator = AnimatorInflater.loadAnimator(requireContext(), animationResId);
        animator.setTarget(view);
        animator.start();
    }

}
