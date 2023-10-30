package com.system.finalcapstoneproject.reportingsystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.system.finalcapstoneproject.ProfileActivity;
import com.system.finalcapstoneproject.R;
import com.system.finalcapstoneproject.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private RelativeLayout chat_button;
    private RelativeLayout profile_button;
    private LinearLayout noReportsLayout;
    private LinearLayout noApprovedReportsLayout;
    private LinearLayout noDeclinedReportsLayout;
    private LinearLayout noUnderInvestigationReportsLayout;
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private TextView reportUserName;
    private Report report;
    private String GET_REPORTS = UrlConstants.GET_REPORTS;
    private String DELETE_REPORTS = UrlConstants.DELETE_REPORTS;
    private TextView allButton;
    private TextView approvedButton;
    private TextView declinedButton;
    private TextView onProcessButton;
    private String user_id;
    private ImageButton backButton;
    private NotificationWebSocketClient notificationWebSocketClient;
    private RelativeLayout loadingProgressBar;
    private TextView reportIDTextView;
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
    private LinearLayout imageContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporting_activity_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, reportList);
        recyclerView.setAdapter(reportAdapter);
        reportUserName = findViewById(R.id.userName);
        chat_button = findViewById(R.id.chat_button);
        profile_button = findViewById(R.id.profile_button);
        allButton = findViewById(R.id.allButton);
        approvedButton = findViewById(R.id.approvedButton);
        declinedButton = findViewById(R.id.declinedButton);
        onProcessButton = findViewById(R.id.investigationButton);
        noReportsLayout = findViewById(R.id.noReportsLayout);
        noApprovedReportsLayout = findViewById(R.id.noApprovedReportsLayout);
        noDeclinedReportsLayout = findViewById(R.id.noDeclinedReportsLayout);
        noUnderInvestigationReportsLayout = findViewById(R.id.noUnderInvestigationReportsLayout);
        loadingProgressBar = findViewById(R.id.loading_screen);
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the progress bar
        backButton = findViewById(R.id.backButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "");
        Log.e("HomeActivity", "retrieveUserDetails - User ID:" + user_id);

        allButton.setBackgroundResource(R.drawable.button_background_all_filled);
        allButton.setTextColor(ContextCompat.getColor(allButton.getContext(), R.color.white));
        approvedButton.setBackgroundResource(R.drawable.button_background_approved);
        declinedButton.setBackgroundResource(R.drawable.button_background_declined);
        onProcessButton.setBackgroundResource(R.drawable.button_background_under_investigation);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChatNotificationSocket();
                finish();
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChatNotificationSocket();
                Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllReports(user_id);
                allButton.setBackgroundResource(R.drawable.button_background_all_filled);
                allButton.setTextColor(ContextCompat.getColor(allButton.getContext(), R.color.white));
                approvedButton.setBackgroundResource(R.drawable.button_background_approved);
                approvedButton.setTextColor(Color.parseColor("#4CAF50"));
                declinedButton.setBackgroundResource(R.drawable.button_background_declined);
                declinedButton.setTextColor(Color.parseColor("#E91E63"));
                onProcessButton.setBackgroundResource(R.drawable.button_background_under_investigation);
                onProcessButton.setTextColor(Color.parseColor("#FFC107"));
            }
        });

        approvedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchApprovedReports(user_id);
                allButton.setBackgroundResource(R.drawable.button_background_all);
                allButton.setTextColor(Color.parseColor("#2196F3"));
                approvedButton.setBackgroundResource(R.drawable.button_background_approved_filled);
                approvedButton.setTextColor(ContextCompat.getColor(approvedButton.getContext(), R.color.white));
                declinedButton.setBackgroundResource(R.drawable.button_background_declined);
                declinedButton.setTextColor(Color.parseColor("#E91E63"));
                onProcessButton.setBackgroundResource(R.drawable.button_background_under_investigation);
                onProcessButton.setTextColor(Color.parseColor("#FFC107"));
            }
        });

        declinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDeclinedReports(user_id);
                allButton.setBackgroundResource(R.drawable.button_background_all);
                allButton.setTextColor(Color.parseColor("#2196F3")); // Blue color
                approvedButton.setBackgroundResource(R.drawable.button_background_approved);
                approvedButton.setTextColor(Color.parseColor("#4CAF50")); // Blue color
                declinedButton.setBackgroundResource(R.drawable.button_background_declined_filled);
                declinedButton.setTextColor(ContextCompat.getColor(declinedButton.getContext(), R.color.white));
                onProcessButton.setBackgroundResource(R.drawable.button_background_under_investigation);
                onProcessButton.setTextColor(Color.parseColor("#FFC107"));
            }
        });

        onProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchOnProcessReports(user_id);
                allButton.setBackgroundResource(R.drawable.button_background_all);
                allButton.setTextColor(Color.parseColor("#2196F3"));
                approvedButton.setBackgroundResource(R.drawable.button_background_approved);
                approvedButton.setTextColor(Color.parseColor("#4CAF50"));
                declinedButton.setBackgroundResource(R.drawable.button_background_declined);
                declinedButton.setTextColor(Color.parseColor("#E91E63"));
                onProcessButton.setBackgroundResource(R.drawable.button_background_under_investigation_filled);
                onProcessButton.setTextColor(ContextCompat.getColor(onProcessButton.getContext(), R.color.white));
            }
        });

        // Set item click listener for the reportAdapter
        reportAdapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                // Start a new method to show the report details summary dialog
                showReportDetailsSummary(position);
            }
        });
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChatNotificationSocket();
                // Open the MainActivity to create a new report
                Intent intent = new Intent(ReportActivity.this, ChatActivity.class);
                startActivity(intent);
                Log.d("Button", "Chat Button was clicked"); // Log the image URL
            }
        });
        // Set menu button click listener
        reportAdapter.setOnMenuClickListener(new ReportAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(int position) {
                // Handle menu button click here
                // Toggle the visibility of Edit, Follow-up, and Delete buttons
                ReportAdapter.ReportViewHolder viewHolder = (ReportAdapter.ReportViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    LinearLayout buttonLayout = viewHolder.itemView.findViewById(R.id.buttonLayout);
                    if (buttonLayout.getVisibility() == View.VISIBLE) {
                        buttonLayout.setVisibility(View.GONE);
                    } else {
                        buttonLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Set edit click listener
        reportAdapter.setOnEditClickListener(new ReportAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position) {
                // Handle edit click here
                report = reportList.get(position);
                String reportId = report.getReportId();

                // Start a new activity to edit the report details
                Intent intent = new Intent(ReportActivity.this, editReport_activity.class);
                intent.putExtra("reportId", reportId);
                startActivity(intent);
                Log.d("Report Adapter", "Edit report with report ID of : " + reportId); // Log the image URL
            }
        });
        // Set delete click listener in the adapter
        reportAdapter.setOnDeleteClickListener(new ReportAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // Show a popup asking the user if they want to delete the report
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setTitle("Delete Report");
                builder.setMessage("Are you sure you want to delete this report?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the report
                        if (position >= 0 && position < reportList.size()) {
                            Report report = reportList.get(position);
                            String reportId = report.getReportId();

                            deleteReportFromDatabase(reportId);

                            // Update the UI by removing the item from the reportList and notifying the adapter
                            reportList.remove(position);
                            reportAdapter.notifyItemRemoved(position);
                            Log.d("Report Adapter", "Delete report with report ID of: " + reportId + " with position of " + position); // Log the image URL
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

        });

        FloatingActionButton createReportButton = findViewById(R.id.createReportButton);
        createReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the MainActivity to create a new report
                Intent intent = new Intent(ReportActivity.this, createReport_activity.class);
                startActivity(intent);
                Log.d("Button", "Create Report Button was clicked"); // Log the image URL
            }
        });

        String firstname = sharedPreferences.getString("firstname", "");
        Log.e("HomeActivity", "retrieveUserDetails - Firstname:" + firstname);
        reportUserName.setText(Html.fromHtml("Hello, " + firstname + "!"));
        checkUserBanStatus(user_id);
    }

    private void showReportDetailsSummary(int position) {
        // Get the report at the clicked position
        Report report = reportList.get(position);

        // Extract the report_id from the report object
        String reportId = report.getReportId();

        // Create and configure the summary dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_report_dialog_summary, null);
        builder.setView(dialogView);

        // Populate the dialog with report details
        reportIDTextView = dialogView.findViewById(R.id.reportIDTextView);
        typeOfCrimeTextView = dialogView.findViewById(R.id.typeOfCrimeTextView);
        dateOfCrimeTextView = dialogView.findViewById(R.id.dateOfCrimeTextView);
        suspectOfCrimeTextView = dialogView.findViewById(R.id.suspectOfCrimeTextView);
        descOfCrimeEditText = dialogView.findViewById(R.id.descOfCrimeEditText);
        nameTextView = dialogView.findViewById(R.id.nameTextView);
        sexTextView = dialogView.findViewById(R.id.sexTextView);
        phoneTextView = dialogView.findViewById(R.id.phoneTextView);
        emailTextView = dialogView.findViewById(R.id.emailTextView);
        imageContainer = dialogView.findViewById(R.id.imageLayout);
        retrieveReportDetailsFromServer(reportId);
        Log.d("JSON Responsssssse", reportId);
        // Show the dialog
        AlertDialog summaryDialog = builder.create();
        summaryDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteReportFromDatabase(final String report_id) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(DELETE_REPORTS);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Create JSON object with the reportId
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("report_id", report_id);
                    Log.e("Report ID:", String.valueOf(requestBody));
                    Log.d("Delete Report Method", "Delete report from database with report ID of: " + requestBody); // Log the image URL
                    // Write the JSON data to the request body
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(requestBody.toString());
                    writer.flush();
                    writer.close();

                    int responseCode = connection.getResponseCode();

                    // Log the response code
                    Log.e("Response Code:", String.valueOf(responseCode));

                    // Read the response from the server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Log the response from the server
                    Log.e("Server Response:", response.toString());

                    // Return true if the request was successful (e.g., HTTP 200 OK)
                    return responseCode == HttpURLConnection.HTTP_OK;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Report deleted successfully
                    // Update the UI by removing the item from the reportList and notifying the adapter
                    int position = findReportPosition(report_id);
                    if (position >= 0) {
                        reportList.remove(position);
                        reportAdapter.notifyItemRemoved(position);
                    }
                } else {
                    String errorMessage = "Failed to delete report";
                    Log.e("ReportActivity", "deleteReportFromDatabase - Error: " + errorMessage);
                }
            }
        }.execute();
    }

    private int findReportPosition(String reportId) {
        for (int i = 0; i < reportList.size(); i++) {
            Report report = reportList.get(i);
            if (report.getReportId().equals(reportId)) {
                return i;
            }
        }
        return -1; // Report not found
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchReports(user_id, "");
    }

    private void fetchReports(final String userId, final String status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "";
                try {
                    // Append the status parameter to the URL
                    URL url = new URL(GET_REPORTS + userId + "&status=" + status);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Success response (HTTP 200 OK)
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();
                        response = responseBuilder.toString();
                        inputStream.close();
                    } else {
                        // Error response (e.g., HTTP 404, 500, etc.)
                        InputStream errorStream = connection.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder errorResponseBuilder = new StringBuilder();
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                errorResponseBuilder.append(errorLine);
                            }
                            errorReader.close();
                            response = errorResponseBuilder.toString();
                            errorStream.close();
                        }
                    }

                    connection.disconnect();

                    final String jsonResponse = response.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseReportResponse(jsonResponse, status);
                        }
                    });
                } catch (IOException e) {
                    Log.e("ReportActivty", "fetchReports - Response String: " + response);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseReportResponse(String response, String passed_status) {
        // Clear the existing report list before parsing the new response
        reportList.clear();
        Log.e("", "" + passed_status);
        try {
            JSONArray jsonArray = new JSONArray(response);
            Log.e("ReportActivty", "parseReportResponse - Response String: " + jsonArray);
            noReportsLayout.setVisibility(View.GONE);
            noApprovedReportsLayout.setVisibility(View.GONE);
            noDeclinedReportsLayout.setVisibility(View.GONE);
            noUnderInvestigationReportsLayout.setVisibility(View.GONE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String report_id = jsonObject.getString("report_id"); // Correctly extract the report_id
                String user_id = jsonObject.getString("user_id"); // Use "id" instead of "reportId"
                String crime_type = jsonObject.getString("crime_type"); // Use "id" instead of "reportId"
                String crime_person = jsonObject.getString("crime_person"); // Use "id" instead of "reportId"
                String crime_location = jsonObject.getString("crime_location");
                String crime_date = jsonObject.getString("crime_date");
                String crime_description = jsonObject.getString("crime_description");
                String crime_time = jsonObject.getString("crime_time");
                String crime_barangay = jsonObject.getString("crime_barangay");
                String crime_user_name = jsonObject.getString("crime_user_name");
                String crime_user_sex = jsonObject.getString("crime_user_sex");
                String crime_user_phone = jsonObject.getString("crime_user_phone");
                String crime_user_email = jsonObject.getString("crime_user_email");
                String report_date = jsonObject.getString("report_date");
                String isUseCurrentLocation = jsonObject.getString("isUseCurrentLocation");
                String isIdentified = jsonObject.getString("isIdentified");
                String status = jsonObject.getString("status");
                String reward_claimed = jsonObject.getString("reward_claimed");

                if (jsonObject.has("imagePaths")) {
                    Log.d("Retrieved Images: ", "There are images"); // Debug log to check the JSON response
                    // Retrieve the image paths associated with the report ID
                    JSONArray imagePathsArray = jsonObject.getJSONArray("imagePaths");
                    for (int j = 0; j < imagePathsArray.length(); j++) {
                        String imagePath = imagePathsArray.getString(j);
                        String imageUrl = UrlConstants.GET_REPORT_IMAGES + imagePath; // Modify the URL as per your server setup
                        Log.d("Retrieved Images: ", imageUrl); // Debug log to check the JSON response
                    }
                }

                Report report = new Report(report_id, user_id, crime_type, crime_person, crime_location, crime_date, crime_description, crime_time, crime_barangay, crime_user_name,
                        crime_user_sex, crime_user_phone, crime_user_email, report_date, isUseCurrentLocation, isIdentified, status, reward_claimed);
                loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
                reportList.add(report);
            }

            // Reverse the order of the reportList to display newest at the top
            Collections.reverse(reportList);

            reportAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
            Log.e("ReportActivty", "parseReportResponse - Response String: " + response);
            if ("".equals(passed_status)) { // Use .equals() for string comparison
                noReportsLayout.setVisibility(View.VISIBLE);
                noApprovedReportsLayout.setVisibility(View.GONE);
                noDeclinedReportsLayout.setVisibility(View.GONE);
                noUnderInvestigationReportsLayout.setVisibility(View.GONE);
                loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
            } else if ("Approved".equals(passed_status)) {
                noApprovedReportsLayout.setVisibility(View.VISIBLE);
                noReportsLayout.setVisibility(View.GONE);
                noDeclinedReportsLayout.setVisibility(View.GONE);
                noUnderInvestigationReportsLayout.setVisibility(View.GONE);
                loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
            } else if ("Declined".equals(passed_status)) {
                noDeclinedReportsLayout.setVisibility(View.VISIBLE);
                noReportsLayout.setVisibility(View.GONE);
                noApprovedReportsLayout.setVisibility(View.GONE);
                noUnderInvestigationReportsLayout.setVisibility(View.GONE);
                loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
            } else if ("Ongoing".equals(passed_status)) {
                noUnderInvestigationReportsLayout.setVisibility(View.VISIBLE);
                noReportsLayout.setVisibility(View.GONE);
                noApprovedReportsLayout.setVisibility(View.GONE);
                noDeclinedReportsLayout.setVisibility(View.GONE);
                loadingProgressBar.setVisibility(View.GONE); // Show the progress bar
            }
            e.printStackTrace();
        }
    }

    private void fetchAllReports(String userId) {
        reportList.clear();
        // Implement code to fetch all reports for the user
        // Update the reportList and notify the adapter
        Log.e("ReportActivty", "fetchAllReports - User ID: " + userId);
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the progress bar
        fetchReports(userId, "");
    }

    private void fetchApprovedReports(String userId) {
        reportList.clear();
        // Implement code to fetch approved reports for the user
        // Update the reportList and notify the adapter
        Log.e("ReportActivty", "fetchApprovedReports - User ID: " + userId);
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the progress bar
        fetchReports(userId, "Approved");
    }

    private void fetchDeclinedReports(String userId) {
        reportList.clear();
        // Implement code to fetch declined reports for the user
        // Update the reportList and notify the adapter
        Log.e("ReportActivty", "fetchDeclinedReports - User ID: " + userId);
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the progress bar
        fetchReports(userId, "Declined");
    }

    private void fetchOnProcessReports(String userId) {
        reportList.clear();
        // Implement code to fetch reports that are on process for the user
        // Update the reportList and notify the adapter
        Log.e("ReportActivty", "fetchOnProcessReports - User ID: " + userId);
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the progress bar
        fetchReports(userId, "Ongoing");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeChatNotificationSocket();
    }

    private void closeChatNotificationSocket() {
        // Close the notificationWebSocketClient when the activity is destroyed
        if (notificationWebSocketClient != null) {
            notificationWebSocketClient.close();
        }
    }

    private void checkUserBanStatus(String userId) {
        Log.e("ReportActivity", "checkUserBanStatus - Checking Ban Status for user with USER ID: " + userId);

        // Make an HTTP request to your server to check the user's ban status
        // Replace "CHECK_BAN_URL" with the actual URL of your server-side script
        String checkBanUrl = UrlConstants.CHECK_BAN_STATUS;

        // Create a request to check the ban status
        StringRequest banCheckRequest = new StringRequest(Request.Method.POST, checkBanUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ReportActivity", "checkUserBanStatus - Response: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isBanned = jsonResponse.getBoolean("isBanned");

                            if (isBanned) {
                                // User is banned, retrieve ban details
                                String banReason = jsonResponse.optString("banReason");
                                String banStartTimestampStr = jsonResponse.optString("banStartTime"); // Correct the field name
                                String banEndTimestampStr = jsonResponse.optString("banEndTime");     // Correct the field name
                                Log.e("ReportActivity", "checkUserBanStatus - jsonResponse: " + jsonResponse);
                                // Check if the date strings are not empty before parsing
                                if (!TextUtils.isEmpty(banStartTimestampStr) && !TextUtils.isEmpty(banEndTimestampStr)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                    Date banStartTimestamp = sdf.parse(banStartTimestampStr);
                                    Date banEndTimestamp = sdf.parse(banEndTimestampStr);

                                    // Get the current date and time
                                    Date currentDate = new Date();
                                    Log.e("ReportActivity", "checkUserBanStatus - currentDate: " + currentDate);
                                    Log.e("ReportActivity", "checkUserBanStatus - banEndTimestamp: " + banEndTimestamp);

                                    // Check if the current date is within the ban period
                                    if (currentDate.before(banEndTimestamp)) {
                                        // User is currently banned, show a ban message
                                        showBanMessage(banReason, banEndTimestamp);
                                    } else {
                                        // User was banned in the past but the ban has expired
                                        // You can handle this case accordingly
                                        // For example, you might allow the user to continue
                                        performAllowedAction();
                                    }
                                } else {
                                    // Handle the case where date strings are empty or malformed
                                    // You can show an error message or take appropriate action
                                }
                            } else {
                                // User is not banned, allow the user to continue
                                performAllowedAction();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error or date parsing error
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ReportActivity", "checkUserBanStatus - VolleyError error: " + error);
                        // Handle the error, e.g., by showing an error message to the user
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId); // Pass the user's ID to check
                return params;
            }
        };

        // Add the request to the Volley request queue to execute it
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(banCheckRequest);
    }

    private void showBanMessage(String banReason, Date banEndTimestamp) {
        // Calculate the remaining time until the ban ends
        long banEndTimeMillis = banEndTimestamp.getTime();

        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are Banned");

        // Inflate the custom layout for the dialog
        View customLayout = getLayoutInflater().inflate(R.layout.custom_ban_dialog, null);
        builder.setView(customLayout);

        // Set the ban reason text in the custom layout
        TextView banReasonText = customLayout.findViewById(R.id.text_ban_reason);
        banReasonText.setText("Reason: " + banReason);

        // Set the remaining time text in the custom layout
        TextView remainingTimeText = customLayout.findViewById(R.id.text_remaining_time);

        // Set the custom "OK" button click listener
        Button customOkButton = customLayout.findViewById(R.id.custom_ok_button);
        customOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set the custom "Chat Admin" button click listener
        Button chatAdminButton = customLayout.findViewById(R.id.chat_admin_button);
        chatAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChatNotificationSocket();
                // Open the MainActivity to create a new report
                Intent intent = new Intent(ReportActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        // Prevent the user from dismissing the dialog
        builder.setCancelable(false);

        // Show the dialog
        AlertDialog dialog = builder.show();

        // Create a handler to update the remaining time text every second
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                long remainingTimeMillis = banEndTimeMillis - currentTimeMillis;

                // Calculate remaining days, hours, minutes, and seconds
                long days = remainingTimeMillis / (1000 * 60 * 60 * 24);
                long hours = (remainingTimeMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (remainingTimeMillis % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (remainingTimeMillis % (1000 * 60)) / 1000;

                // Update the remaining time text
                remainingTimeText.setText("Ban will be lifted in: " + days + " days, " + hours + " hours, " + minutes + " minutes, and " + seconds + " seconds.");

                // Check if the ban has ended and dismiss the dialog
                if (remainingTimeMillis <= 0) {
                    dialog.dismiss();
                    handler.removeCallbacks(this); // Stop the handler
                } else {
                    // Schedule the runnable to run again after 1 second
                    handler.postDelayed(this, 1000);
                }
            }
        };

        // Start the handler to update the remaining time
        handler.post(runnable);
    }

    private void performAllowedAction() {
        // This method is called when the user is not banned or the ban has expired
        // You can continue with actions that are allowed for the user
        // For example, fetching reports
        fetchReports(user_id, "");
    }

    private void retrieveReportDetailsFromServer(final String reportId) {
        Log.d("JSON Response", reportId);
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Retrieve the report details from the server using the reportId
                // Make an HTTP request to the server and parse the response JSON to populate userData
                Log.e("editReportActivity", "retrieveReportDetailsFromServer - has started"); // Debug log
                // Example of retrieving report details from the server

                // Perform the HTTP request and retrieve the response
                String jsonResponse = performHttpRequest(UrlConstants.GET_REPORT_DETAILS + reportId);

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
                    String crime_time = jsonObject.getString("crime_time");
                    Double crime_location_latitude = Double.valueOf(jsonObject.getString("crime_location_latitude"));
                    Double crime_location_longitude = Double.valueOf(jsonObject.getString("crime_location_longitude"));
                    String crime_description = jsonObject.getString("crime_description");
                    String crime_user_name = jsonObject.getString("crime_user_name");
                    String crime_user_sex = jsonObject.getString("crime_user_sex");
                    String crime_user_phone = jsonObject.getString("crime_user_phone");
                    String crime_user_email = jsonObject.getString("crime_user_email");
                    String report_date = jsonObject.getString("report_date");

                    if (jsonObject.has("imagePaths")) {

                        // Retrieve the image paths associated with the report ID
                        JSONArray imagePathsArray = jsonObject.getJSONArray("imagePaths");
                        for (int i = 0; i < imagePathsArray.length(); i++) {
                            String imagePath = imagePathsArray.getString(i);
                            String imageUrl = UrlConstants.GET_REPORT_IMAGES + imagePath; // Modify the URL as per your server setup
                            displayImages(Collections.singletonList(imageUrl));
                            Log.d("Retrieved Images: ", imagePath); // Debug log to check the JSON response
                        }

                    } else {
                        Log.e("JSON", "No value for imagePaths key");
                    }
                    reportIDTextView.setText("Report ID: " + report_id);
                    typeOfCrimeTextView.setText(crime_type);
                    dateOfCrimeTextView.setText(crime_date);
                    suspectOfCrimeTextView.setText(crime_person);
                    descOfCrimeEditText.setText(crime_description);
                    nameTextView.setText(crime_user_name);
                    sexTextView.setText(crime_user_sex);
                    phoneTextView.setText(crime_user_phone);
                    emailTextView.setText(crime_user_email);
                    // Set other views with report details

                    // Set up dialog buttons or actions as needed
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
            Toast.makeText(ReportActivity.this, "Failed to retrieve report details", Toast.LENGTH_SHORT).show();
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
    private void displayImages(List<String> imageUrl) {
        Log.d("MainActivity", "displayImages - has started");
        Log.e("EditReportStep2Fragment", "displayImages - Images from the Database: " + imageUrl);

        // Use "this" to access the current activity's context
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> newImageUrls = new ArrayList<>(); // Create a list for new image URLs

                for (final String imageUrl : imageUrl) {
                    // Create a new FrameLayout to hold the ImageView and delete button
                    FrameLayout imageLayout = new FrameLayout(ReportActivity.this); // Replace "MainActivity" with your activity's name
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
                    ImageView imageView = new ImageView(ReportActivity.this); // Replace "MainActivity" with your activity's name
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


                    // Add the ImageView and delete button to the imageLayout
                    imageLayout.addView(imageView);
                    // Add the ImageView (delete button) to the imageLayout
                    // Add the imageLayout to the imageContainer
                    imageContainer.addView(imageLayout);

                }
            }
        });
    }

}
