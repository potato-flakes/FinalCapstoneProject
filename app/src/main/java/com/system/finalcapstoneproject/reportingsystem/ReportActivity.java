package com.system.finalcapstoneproject.reportingsystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.system.finalcapstoneproject.R;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private LinearLayout chat_button;
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private TextView reportUserName;
    private Report report;
    private String GET_REPORTS = UrlConstants.GET_REPORTS;
    private String DELETE_REPORTS = UrlConstants.DELETE_REPORTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporting_activity_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(reportAdapter);
        reportUserName = findViewById(R.id.reportUserName);
        chat_button = findViewById(R.id.chat_button);

        // Set item click listener
        reportAdapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("Report Adapter", "A report was clicked"); // Log the image URL
                // Handle item click here
                // Start a new activity to display the report details
                // Pass the report ID or other identifier as an intent extra
            }
        });
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void fetchReports(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "";
                try {
                    URL url = new URL(GET_REPORTS + userId);
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
                            parseReportResponse(jsonResponse);
                        }
                    });
                } catch (IOException e) {
                    Log.e("ReportActivty", "fetchReports - Response String: " + response); // Use the declared response variable
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve user ID from the intent or wherever you store it
        String userId = "9183797"; // Replace with the actual user ID

        fetchReports(userId);
    }

    private void parseReportResponse(String response) {
        // Clear the existing report list before parsing the new response
        reportList.clear();

        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String report_id = jsonObject.getString("report_id"); // Correctly extract the report_id
                String user_id = jsonObject.getString("user_id"); // Use "id" instead of "reportId"
                String firstname = jsonObject.getString("firstname"); // Use "id" instead of "reportId"
                String lastname = jsonObject.getString("lastname"); // Use "id" instead of "reportId"
                String description = jsonObject.getString("crime_type");
                String location = jsonObject.getString("crime_location");
                String date = jsonObject.getString("crime_date");
                String time = jsonObject.getString("crime_time");

                Report report = new Report(report_id, user_id, firstname, lastname, description, location, date, time);
                String lastName = "<b>" + report.getLastname() + "</b>";
                reportUserName.setText(Html.fromHtml("Hello, " + lastName + "!"));

                reportList.add(report);
            }

            // Reverse the order of the reportList to display newest at the top
            Collections.reverse(reportList);

            reportAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("ReportActivty", "parseReportResponse - Response String: " + response); // Use the declared response variable
            e.printStackTrace();
        }
    }


}
